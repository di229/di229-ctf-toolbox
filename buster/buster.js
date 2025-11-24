import pLimit from 'p-limit';
import axios from 'axios';
import { readFile } from 'fs/promises';
import { Agent } from 'https';

function trimls(part) {
  return (part[0] === '/' ? part.substring(1) : part);
}

async function get(host, path="/") {
  let url = host + '/' + trimls(path);
  const agent = new Agent({
    rejectUnauthorized: false,
  });
  return axios
    .get(`https://${url}`, {
      httpsAgent: agent,
      validateStatus: (st) => st !== 404,
    })
    // Note. then() is an asynchronous callback.
    .then((res) => [res.status, url])
    .catch((e) => {
      if (e.response) {
        return [e.response.status, url];
      }
      throw e;
    });
}

//Note. a function must be async to use await
async function main(host, prefix_file, wordlist_file, delay=128) {
  let prefixes = await readconf(prefix_file);
  let suffixes = await readconf(wordlist_file)
  let paths = [];
  for (const p of prefixes) {
    for (const s of suffixes) {
      paths.push(p +'/' + trimls(s));
    }
  }
  console.log(`Number of urls to try: ${paths.length}. Hold on.`);
  const limit = pLimit(delay);
  const r = await Promise.allSettled(paths.map(path => 
    limit(() => get(host, path))));
  return r;
}

async function readconf(filename) {
  try {
    let packages = await readFile(filename, 'utf8');
    return packages.split("\n").filter((p) => p !== '');
  } catch(e) {
    console.log(e);
    return [];
  }
}

//Note. all async functions return a promise - extract with .then
main('127.0.3.1', 'packages.txt', '/usr/share/SecLists/Discovery/Web-Content/quickhits.txt')
  .then(r => {
  for (let result of r.filter((prom) => prom.status === 'fulfilled')) {
    if (result.value[0] !== 404) {
      console.log(result.value);
    }
  }
}); 

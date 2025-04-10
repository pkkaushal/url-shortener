import http from 'k6/http';
import { check, sleep } from 'k6';

export let options = {
  stages: [
    { duration: '1m', target: 10 },  // Adjust based on your needs
    { duration: '5m', target: 50 },
    { duration: '5m', target: 0 },
  ],
};

const baseUrl = 'http://localhost:8080/api';  // Your Spring Boot application URL

function generateRandomLongUrl() {
  return `https://example.com/${Math.random().toString(36).substr(2, 9)}`;
}

export default function () {
  // Test POST /shorten
  const longUrl = generateRandomLongUrl();
  const shortenResponse = http.post(
    `${baseUrl}/shorten`,
    longUrl,  // Send JSON payload
    { headers: { 'Content-Type': 'text/plain' } }
  );
  //console.log(shortenResponse);

  check(shortenResponse, {
    'shorten returns 200': (r) => r.status === 200,
  });

  if (shortenResponse.status !== 200) {
    console.error('Error in /shorten response:', shortenResponse.body);
    return;  // Skip further steps if POST failed
  }

  //const shortenData = shortenResponse.json();  // Parse the response JSON
  const shortCode = shortenResponse.body;     // Extract the short code
  console.log('Short Code:', shortCode);

 if (shortCode) {
     const redirectResponse = http.get(`${baseUrl}/redirect?code=${shortCode}`, {
       // Disabling automatic redirect following in k6
       redirects: 0
     });
//     console.log('Request URL:', `${baseUrl}/redirect?code=${shortCode}`);
//     console.log('Request Status Code:', redirectResponse.status, redirectResponse);
  // console.log(redirectResponse);
   check(redirectResponse, {
     'redirect returns 302': (r) => (r.status === 302 || r.status===200),
   });

   if (redirectResponse.status !== 302) {


     console.error('Error in /redirect response:',redirectResponse.status);
   }
 } else {
   console.error('Short code is undefined or invalid');
 }

//  if (redirectResponse.status !== 302) {
//    console.error('Error in /redirect response:', redirectResponse.body);
//  }

  sleep(1);  // Simulate real user delay
}

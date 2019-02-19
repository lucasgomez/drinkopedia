let backendHost;

const hostname = window && window.location && window.location.hostname;
// if(hostname === 'http://soif.fetedelabiere.ch/') {
  // backendHost = 'http://drinks.elveteek.ch';
// } else {
  backendHost = process.env.REACT_APP_BACKEND_HOST || 'http://localhost:8081/drinkopedia';
// }

export const API_ROOT = `${backendHost}`;

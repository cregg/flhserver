var ENV = {}

if (process.env.NODE_ENV === 'production') {
  ENV.apiURL = "https://flhserver.herokuapp.com/v1/api/"
} else {
  ENV.apiURL = "localhost:8080/v1/api"
}



export default ENV

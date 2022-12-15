import axios from "axios";

export default axios.create({
  baseURL: "http://localhost/whatsonmypath",
  headers: {
    "Content-Type": "application/json",
  },
});

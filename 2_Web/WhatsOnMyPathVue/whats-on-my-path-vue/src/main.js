import { createApp } from "vue";
import App from "./App.vue";
import router from "./router";
import store from "./store";

import "bootstrap/dist/css/bootstrap.min.css";
import "bootstrap";

// Vensor CSS Files
// import "@/assets/vendor/aos/aos.css";
// import "@/assets/vendor/bootstrap-icons/bootstrap-icons.css";
// import "@/assets/vendor/boxicons/css/boxicons.min.css";
// import "@/assets/vendor/glightbox/css/glightbox.min.css";
// import "@/assets/vendor/swiper/swiper-bundle.min.css";

// Template Main CSS File
import "@/assets/css/style.css";

// Favicons
// import "@/assets/img/favicon.png";
// import "@/assets/img/apple-touch-icon.png";

// Google Fonts
// import "@/assets/css/gfonts.css";

// Vendor JS Files
// import "@/assets/vendor/purecounter/purecounter_vanilla.js";
// import "@/assets/vendor/aos/aos.js";
// import "@/assets/vendor/bootstrap/js/bootstrap.bundle.min.js";
// import "@/assets/vendor/glightbox/js/glightbox.min.js";
// import "@/assets/vendor/isotope-layout/isotope.pkgd.min.js";
// import "@/assets/vendor/swiper/swiper-bundle.min.js";
// import "@/assets/vendor/waypoints/noframework.waypoints.js";
// import "@/assets/vendor/php-email-form/validate.js";

// Template Main JS File
import "@/assets/js/main.js";

// jQuery
import jQuery from "jquery";
window.jQuery = window.$ = jQuery;

createApp(App).use(store).use(router).mount("#app");

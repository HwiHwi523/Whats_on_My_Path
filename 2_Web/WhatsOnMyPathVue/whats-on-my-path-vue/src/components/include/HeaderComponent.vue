<template>
  <!-- ======= Header ======= -->
  <header id="header" class="fixed-top">
    <div
      class="container-fluid d-flex justify-content-between align-items-center"
    >
      <h1 class="logo me-auto me-lg-0">
        <router-link :to="{ name: 'Index' }">WoMP</router-link>
      </h1>
      <nav id="navbar" class="navbar order-last order-lg-0">
        <ul>
          <li>
            <router-link :to="{ name: 'Index' }" @click="moveRouter">
              <h4 class="hangeul">홈으로</h4>
            </router-link>
          </li>
          <li>
            <router-link :to="{ name: 'About' }" @click="moveRouter">
              <h4 class="hangeul">소개</h4>
            </router-link>
          </li>
          <li>
            <router-link :to="{ name: 'FindPath' }" @click="moveRouter">
              <h4 class="hangeul">길찾기</h4>
            </router-link>
          </li>
        </ul>
        <i class="bi bi-list mobile-nav-toggle" @click="clickMobileNavbar"></i>
      </nav>
      <!-- .navbar -->

      <div class="header-social-links">
        <a href="#" class="github"><i class="bi bi-github"></i></a>
      </div>
    </div>
  </header>
  <!-- End Header -->
</template>

<script>
export default {
  methods: {
    clickMobileNavbar(event) {
      document.querySelector("#navbar").classList.toggle("navbar-mobile");
      event.target.classList.toggle("bi-list");
      event.target.classList.toggle("bi-x");
    },
    moveRouter() {
      // 모바일 내비바 끄기
      let mobileNavbar = document.querySelector(".navbar-mobile");
      if (mobileNavbar) {
        mobileNavbar.classList.toggle("navbar-mobile");
      }

      // 모바일 내비바 버튼 토글 (O -> X, X -> O)
      let mobileNavbarBtn = document.querySelector(".mobile-nav-toggle");
      if (mobileNavbarBtn) {
        mobileNavbarBtn.classList.toggle("bi-list");
        mobileNavbarBtn.classList.toggle("bi-x");
      }
    },
  },
  mounted() {
    /**
     * Easy selector helper function
     */
    const select = (el, all = false) => {
      el = el.trim();
      if (all) {
        return [...document.querySelectorAll(el)];
      } else {
        return document.querySelector(el);
      }
    };

    /**
     * Easy event listener function
     */
    const on = (type, el, listener, all = false) => {
      let selectEl = select(el, all);
      if (selectEl) {
        if (all) {
          selectEl.forEach((e) => e.addEventListener(type, listener));
        } else {
          selectEl.addEventListener(type, listener);
        }
      }
    };

    /**
     * Scrolls to an element with header offset
     */
    const scrollto = (el) => {
      let header = select("#header");
      let offset = header.offsetHeight;

      let elementPos = select(el).offsetTop;
      window.scrollTo({
        top: elementPos - offset,
        behavior: "smooth",
      });
    };

    /**
     * Scroll with offset on links with a class name .scrollto
     */
    on(
      "click",
      ".scrollto",
      function (e) {
        if (select(this.hash)) {
          e.preventDefault();

          let navbar = select("#navbar");
          if (navbar.classList.contains("navbar-mobile")) {
            navbar.classList.remove("navbar-mobile");
            let navbarToggle = select(".mobile-nav-toggle");
            navbarToggle.classList.toggle("bi-list");
            navbarToggle.classList.toggle("bi-x");
          }
          scrollto(this.hash);
        }
      },
      true
    );
  },
};
</script>

<template>
  <!-- ======= Hero Section ======= -->
  <section id="hero" class="restaurant d-flex align-items-center">
    <div
      class="container d-flex flex-column align-items-center"
      data-aos="zoom-in"
      data-aos-delay="100"
    >
      <h1>What's on My Path ?</h1>
      <h2 class="hangeul">내가 가는 길에는 어떤 음식점이 있을까?</h2>
      <router-link :to="{ name: 'FindPath' }" class="btn-about">
        <span class="hangeul-air">찾으러 가기</span>
      </router-link>
    </div>
  </section>
  <!-- End Hero -->
</template>

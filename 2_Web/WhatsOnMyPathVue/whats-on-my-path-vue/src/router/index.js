import { createRouter, createWebHistory } from "vue-router";

import IndexContent from "@/components/IndexContent.vue";
import AboutContent from "@/components/AboutContent.vue";

import PathView from "@/views/PathView.vue";
import FindPath from "@/components/FindPath/SetPlaceContent.vue";
import ResultPath from "@/components/FindPath/FindResultContent.vue";

const routes = [
  {
    path: "/",
    name: "Index",
    component: IndexContent,
  },
  {
    path: "/about",
    name: "About",
    // route level code-splitting
    // this generates a separate chunk (about.[hash].js) for this route
    // which is lazy-loaded when the route is visited.
    component: AboutContent,
  },
  {
    path: "/path",
    name: "PathView",
    component: PathView,
    children: [
      {
        path: "find",
        name: "FindPath",
        component: FindPath,
      },
      {
        path: "result",
        name: "ResultPath",
        component: ResultPath,
      },
    ],
  },
];

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes,
});

export default router;

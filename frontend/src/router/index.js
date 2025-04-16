import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/login',
      name: 'login',
      component: () => import('../views/Login.vue'),
    },
    {
      path: '/register',
      name: 'register',
      component: () => import('../views/Register.vue'),
    },
    {
      path: '/workouts',
      name: 'workouts',
      component: () => import('../views/Workouts.vue'),
    },
    {
      //path: '/:catchAll(.*)',
      //name: 'not-found',
      //component: () => import('../views/NotFoundView.vue'),
    },
  ],
})

export default router

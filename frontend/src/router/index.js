import { createRouter, createWebHistory } from 'vue-router'
import AuthLayout from '@/Layout/AuthLayout.vue'
import MainLayout from '@/Layout/MainLayout.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/auth',
      component: AuthLayout,
      children: [
        {
          path: 'login',
          name: 'login',
          component: () => import('../views/Login.vue'),
        },
        {
          path: 'register',
          name: 'register',
          component: () => import('../views/Register.vue'),
        },
      ],
    },
    {
      path: '/',
      component: MainLayout,
      children: [
        {
          path: '',
          name: 'dashboard',
          component: () => import('../views/Dashboard.vue'),
        },
        {
          path: 'workouts',
          name: 'workouts',
          component: () => import('../views/WorkoutsList.vue'),
        },
        {
          path: 'workoutedit',
          name: 'workoutedit',
          component: () => import('../views/WorkoutEdit.vue'),
        },
        {
          path: 'workout',
          name: 'workout',
          component: () => import('../views/Workout.vue'),
        },
        {
          path: 'progress',
          name: 'progress',
          component: () => import('../views/Progress.vue'),
        },
        {
          path: 'clients',
          name: 'clients',
          component: () => import('../views/Clients.vue'),
        },
      ],
    },
    // {
    //   path: '/:catchAll(.*)',
    //   name: 'not-found',
    //   component: () => import('../views/NotFoundView.vue'),
    // },
  ],
})

export default router

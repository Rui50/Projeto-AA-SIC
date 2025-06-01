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
          path: 'workout/edit/:id',
          name: 'workoutedit',
          component: () => import('../views/WorkoutEdit.vue'),
        },
        {
          path: 'workout/:id',
          name: 'workout',
          component: () => import('../views/WorkoutPlanDetails.vue'),
        },
        {
          path: 'workout/execution/:id',
          name: 'workoutexecution',
          component: () => import('../views/WorkoutExecution.vue'),
        },
        {
          path: 'progress',
          name: 'progress',
          component: () => import('../views/Progress.vue'),
        },
        {
          path: 'clients',
          name: 'clients',
          component: () => import('../views/ClientsList.vue'),
        },
        {
          path: 'client/:id',
          name: 'clientInfo',
          component: () => import('../views/ClientInfo.vue'),
        }
      ],
    },
    // {
    //   path: '/:catchAll(.*)',
    //   name: 'not-found',
    //   component: () => import('../views/NotFoundView.vue'),
    // },
  ],
})

/* router.beforeEach((to, from, next) => {
  // Add any global navigation guards here if needed
  if (to.meta.requiresAuth && !isAuthenticated) {
        next('/login');
    } else if (to.name === 'Login' && isAuthenticated) {
        next('/');
    } else {
        next();
    }
})*/

export default router

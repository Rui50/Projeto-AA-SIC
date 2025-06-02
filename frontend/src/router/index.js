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
          meta: { requiresAuth: true },
          component: () => import('../views/Dashboard.vue'),
        },
        {
          path: 'workouts',
          name: 'workouts',
          meta: { requiresAuth: true },
          component: () => import('../views/WorkoutsList.vue'),
        },
        {
          path: 'workout/edit/:id',
          name: 'workoutedit',
          meta: { requiresAuth: true },
          component: () => import('../views/WorkoutEdit.vue'),
        },
        {
          path: 'workout/:id',
          name: 'workout',
          meta: { requiresAuth: true },
          component: () => import('../views/WorkoutPlanDetails.vue'),
        },
        {
          path: 'workout/execution/:id',
          name: 'workoutexecution',
          meta: { requiresAuth: true },
          component: () => import('../views/WorkoutExecution.vue'),
        },
        {
          path: 'progress',
          name: 'progress',
          meta: { requiresAuth: true },
          component: () => import('../views/Progress.vue'),
        },
        {
          path: 'clients',
          name: 'clients',
          meta: { requiresAuth: true },
          component: () => import('../views/ClientsList.vue'),
        },
        {
          path: 'client/:id',
          name: 'clientInfo',
          meta: { requiresAuth: true },
          component: () => import('../views/ClientInfo.vue'),
        },
        {
          path: 'workouts/history/:id',
          name: 'workoutsHistory',
          meta: { requiresAuth: true },
          component: () => import('../views/WorkoutsHistory.vue'),
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
import { useUserStore } from '../stores/userStore'
router.beforeEach((to, from, next) => {
  const userStore = useUserStore()

  if (to.meta.requiresAuth && !userStore.isAuthenticated) {
    next('/auth/login')
  } else if (to.name === 'login' && userStore.isAuthenticated) {
    next('/')
  } else {
    next()
  }
})

export default router

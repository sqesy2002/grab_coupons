// 确保从 'vue-router' 中正确导入了 createRouter 和 createWebHistory
import { createRouter, createWebHistory } from 'vue-router'

import Layout from '../components/Layout.vue'
import UserInfo from '../views/UserInfo.vue'
import CouponInfo from '../views/CouponInfo.vue'
import GrabCoupon from '../views/GrabCoupon.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      component: Layout,
      redirect: '/user',
      children: [
        {
          path: 'user',
          name: 'UserInfo',
          component: UserInfo
        },
        {
          path: 'coupon',
          name: 'CouponInfo',
          component: CouponInfo
        },
        {
          path: 'grab',
          name: 'GrabCoupon',
          component: GrabCoupon
        }
      ]
    }
  ]
})

export default router
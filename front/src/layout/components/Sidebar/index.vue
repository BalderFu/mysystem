<template>
  <div class="sidebar-container">
    <el-menu
      :default-active="activeMenu"
      :background-color="variables.menuBg"
      :text-color="variables.menuText"
      :active-text-color="variables.menuActiveText"
      :unique-opened="false"
      :collapse-transition="false"
      mode="vertical"
    >
      <sidebar-item
        v-for="route in permissionRoutes"
        :key="route.path"
        :item="route"
        :base-path="route.path"
      />
    </el-menu>
  </div>
</template>

<script>
import SidebarItem from './SidebarItem'
import variables from '@/styles/variables.scss'
import Constants from '@/utils/constants'

export default {
  components: { SidebarItem },
  computed: {
    activeMenu() {
      const route = this.$route
      const { meta, path } = route
      if (meta.activeMenu) {
        return meta.activeMenu
      }
      return path
    },
    userInfo() {
      try {
        const userStr = localStorage.getItem(Constants.ID.USER_KEY)
        return userStr ? JSON.parse(userStr) : null
      } catch (error) {
        console.error('Failed to parse user info:', error)
        return null
      }
    },
    permissionRoutes() {
      const userRole = this.userInfo?.role
      console.log('Current user role:', userRole) // 调试用

      return this.$router.options.routes.filter(route => {
        if (!route.children) {
          return false
        }

        const filteredChildren = route.children.filter(child => {
          if (!child.meta) {
            return true
          }

          if (!child.meta.roles) {
            return true
          }

          console.log('Menu roles:', child.meta.roles) // 调试用
          return child.meta.roles.includes(userRole)
        })

        if (filteredChildren.length > 0) {
          route.children = filteredChildren
          return true
        }

        return false
      })
    },
    variables() {
      return variables
    }
  },
  mounted() {
    // 调试用
    console.log('User info from localStorage:', this.userInfo)
    console.log('All routes:', this.$router.options.routes)
    console.log('Filtered routes:', this.permissionRoutes)
  }
}
</script>

<style lang="scss" scoped>
.sidebar-container {
  height: 100%;
  background-color: var(--menu-bg-color);
  
  .el-menu {
    border: none;
    height: 100%;
    width: 100% !important;
  }
}
</style> 
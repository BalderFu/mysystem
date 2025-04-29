<template>
  <div class="dynamic-login-background">
    <div class="shapes">
      <div class="shape shape-1"></div>
      <div class="shape shape-2"></div>
      <div class="shape shape-3"></div>
      <div class="shape shape-4"></div>
      <div class="shape shape-5"></div>
    </div>
    <ul class="particles">
      <li v-for="i in 20" :key="i"></li>
    </ul>
  </div>
</template>

<script>
export default {
  name: 'DynamicLoginBackground'
}
</script>

<style lang="scss" scoped>
.dynamic-login-background {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  overflow: hidden;
  /* 设置为新的浅粉色渐变 */
  background: linear-gradient(
    170deg, /* 使用柔和的角度 */
    #ffdde1 0%, /* 从非常浅的粉色开始 */
    #ee9ca7 100% /* 过渡到柔和玫瑰粉 */
  );
  z-index: 0; /* 确保在登录框下方 */
}

/* 漂浮形状 */
.shapes {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  overflow: hidden;
  /* 在浅色背景下，透明度可以高一些 */
  opacity: 0.8; 
  
  .shape {
    position: absolute;
    list-style: none;
    display: block;
    border-radius: 50%;
    /* 使用浅粉色或白色半透明 */
    background-color: rgba(255, 255, 255, 0.15); /* 白色半透明 */
    animation: float 25s linear infinite;
    bottom: -150px;

    /* 保持形状的其他样式 */
    &.shape-1 { left: 25%; width: 80px; height: 80px; animation-delay: 0s; }
    &.shape-2 { left: 10%; width: 150px; height: 150px; animation-delay: 2s; animation-duration: 17s; background-color: rgba(238, 156, 167, 0.1); } /* 使用 #ee9ca7 的浅透明色 */
    &.shape-3 { left: 70%; width: 100px; height: 100px; animation-delay: 4s; }
    &.shape-4 { left: 40%; width: 60px; height: 60px; animation-delay: 0s; animation-duration: 18s; background-color: rgba(255, 255, 255, 0.2); }
    &.shape-5 { left: 65%; width: 120px; height: 120px; animation-delay: 3s; background-color: rgba(238, 156, 167, 0.08); } 
  }
}

@keyframes float {
  0% {
    transform: translateY(0) rotate(0deg);
    opacity: 1;
    border-radius: 50%;
  }
  100% {
    transform: translateY(-1000px) rotate(720deg);
    opacity: 0;
    border-radius: 40%;
  }
}

/* 粒子效果 */
.particles {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  padding: 0;
  margin: 0;
  overflow: hidden;
  /* 在浅色背景下，透明度可以高一些 */
  opacity: 0.7;
  
  li {
    position: absolute;
    display: block;
    list-style: none;
    width: 20px;
    height: 20px;
    /* 使用浅粉色或白色半透明 */
    background: rgba(255, 255, 255, 0.2); /* 白色半透明 */
    animation: particle-animate 20s linear infinite;
    bottom: -150px;
    border-radius: 50%;

    /* 调整粒子颜色 */
    &:nth-child(1) { background: rgba(238, 156, 167, 0.25); /* ... */ }
    &:nth-child(3) { background: rgba(255, 255, 255, 0.15); /* ... */ }
    &:nth-child(5) { background: rgba(238, 156, 167, 0.2); /* ... */ }
    &:nth-child(7) { background: rgba(255, 255, 255, 0.18); /* ... */ }
    &:nth-child(9) { background: rgba(238, 156, 167, 0.28); /* ... */ }
    &:nth-child(11) { background: rgba(255, 255, 255, 0.16); /* ... */ }
    &:nth-child(13) { background: rgba(238, 156, 167, 0.22); /* ... */ }
    &:nth-child(15) { background: rgba(255, 255, 255, 0.15); /* ... */ }
    &:nth-child(17) { background: rgba(238, 156, 167, 0.18); /* ... */ }
    &:nth-child(18) { background: rgba(255, 255, 255, 0.2); /* ... */ }
    &:nth-child(20) { background: rgba(238, 156, 167, 0.25); /* ... */ }
    
    /* 保留其他 nth-child 样式 */
    &:nth-child(1) { left: 10%; width: 8px; height: 8px; animation-delay: 0s; animation-duration: 12s; }
    &:nth-child(2) { left: 20%; width: 12px; height: 12px; animation-delay: 1s; animation-duration: 15s; }
    &:nth-child(3) { left: 30%; width: 6px; height: 6px; animation-delay: 3s; animation-duration: 18s; }
    &:nth-child(4) { left: 40%; width: 10px; height: 10px; animation-delay: 0.5s; animation-duration: 13s; }
    &:nth-child(5) { left: 50%; width: 7px; height: 7px; animation-delay: 2s; animation-duration: 16s; }
    &:nth-child(6) { left: 60%; width: 11px; height: 11px; animation-delay: 4s; animation-duration: 14s; }
    &:nth-child(7) { left: 70%; width: 5px; height: 5px; animation-delay: 1.5s; animation-duration: 20s; }
    &:nth-child(8) { left: 80%; width: 9px; height: 9px; animation-delay: 5s; animation-duration: 11s; }
    &:nth-child(9) { left: 90%; width: 13px; height: 13px; animation-delay: 2.5s; animation-duration: 17s; }
    &:nth-child(10) { left: 15%; width: 7px; height: 7px; animation-delay: 6s; animation-duration: 19s; }
    &:nth-child(11) { left: 25%; width: 10px; height: 10px; animation-delay: 7s; animation-duration: 12s; }
    &:nth-child(12) { left: 35%; width: 8px; height: 8px; animation-delay: 8s; animation-duration: 15s; }
    &:nth-child(13) { left: 45%; width: 12px; height: 12px; animation-delay: 6.5s; animation-duration: 18s; }
    &:nth-child(14) { left: 55%; width: 6px; height: 6px; animation-delay: 9s; animation-duration: 13s; }
    &:nth-child(15) { left: 65%; width: 9px; height: 9px; animation-delay: 7.5s; animation-duration: 16s; }
    &:nth-child(16) { left: 75%; width: 11px; height: 11px; animation-delay: 10s; animation-duration: 14s; }
    &:nth-child(17) { left: 85%; width: 7px; height: 7px; animation-delay: 8.5s; animation-duration: 20s; }
    &:nth-child(18) { left: 5%; width: 10px; height: 10px; animation-delay: 11s; animation-duration: 11s; }
    &:nth-child(19) { left: 95%; width: 8px; height: 8px; animation-delay: 9.5s; animation-duration: 17s; }
    &:nth-child(20) { left: 50%; width: 6px; height: 6px; animation-delay: 12s; animation-duration: 19s; }
  }
}

@keyframes particle-animate {
  0% {
    transform: translateY(0) rotate(0deg);
    opacity: 1;
  }
  100% {
    transform: translateY(-1000px) rotate(360deg);
    opacity: 0;
  }
}

</style> 
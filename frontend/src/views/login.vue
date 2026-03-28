<template>
  <div class="login-container">
    <canvas ref="canvasRef" class="background-canvas"></canvas>
    
    <div class="login-content">
      <div class="brand-section">
        <h1 class="brand-title">Zenith Flow</h1>
        <p class="brand-slogan">Experience the future of workflow.</p>
      </div>
      
      <div class="login-card">
        <div class="login-header">
          <h2>{{ $t("ui.login.loginBtn") }}</h2>
          <div class="lang-switch">
             <lang>
                <svg-icon name="fanyiline" class="icon-lang"></svg-icon>
             </lang>
          </div>
        </div>

        <el-form ref="formRef" :model="login" :rules="rules" @keyup.enter="onLogin" class="login-form">
          <el-form-item prop="username">
            <el-input 
              v-model="login.username" 
              :placeholder="$t('ui.login.userNamePlaceholder')" 
              class="custom-input"
            >
              <template #prefix>
                <svg-icon name="user" class="input-icon"></svg-icon>
              </template>
            </el-input>
          </el-form-item>
          
          <el-form-item prop="password">
            <el-input 
              v-model="login.password" 
              :placeholder="$t('ui.login.passwordPlaceholder')" 
              type="password" 
              show-password
              class="custom-input"
            >
              <template #prefix>
                <svg-icon name="lock" class="input-icon"></svg-icon>
              </template>
            </el-input>
          </el-form-item>

          <el-button 
            type="primary" 
            :loading="state.loading" 
            @click="onLogin" 
            class="login-btn"
          >
            {{ $t("ui.login.loginBtn") }}
          </el-button>
        </el-form>

        <div class="login-footer">
           <span>{{ state.year }} © {{ $t("ui.app.copyright") }}</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { onMounted, onUnmounted, reactive, ref } from "vue";
import { CacheToken } from "@/constants/cacheKey";
import Lang from "@/components/base/lang/index";
import baseService from "@/service/baseService";
import { setCache } from "@/utils/cache";
import { ElMessage } from "element-plus";
import SvgIcon from "@/components/base/svg-icon/index";
import { useAppStore } from "@/store";
import { useRouter } from "vue-router";
import { useI18n } from "vue-i18n";

const store = useAppStore();
const router = useRouter();
const { t } = useI18n();

const state = reactive({
  loading: false,
  year: new Date().getFullYear()
});

const login = reactive({ username: "", password: "" });
const canvasRef = ref<HTMLCanvasElement | null>(null);

onMounted(() => {
  store.logout();
  initThree();
});

onUnmounted(() => {
  window.removeEventListener('resize', resizeCanvas);
  window.removeEventListener('mousemove', onMouseMove);
  if (animationFrameId) {
    cancelAnimationFrame(animationFrameId);
  }
  // Clean up Three.js resources
  if (renderer) renderer.dispose();
  if (scene) scene.clear();
});

const formRef = ref();

const rules = ref({
  username: [{ required: true, message: t("validate.required"), trigger: "blur" }],
  password: [{ required: true, message: t("validate.required"), trigger: "blur" }]
});

const onLogin = () => {
  formRef.value.validate((valid: boolean) => {
    if (valid) {
      state.loading = true;
      baseService
        .post("/login", login)
        .then((res) => {
          state.loading = false;
          if (res.code === 0) {
            setCache(CacheToken, res.data, true);
            ElMessage.success(t("ui.login.loginOk"));
            router.push("/");
          } else {
            ElMessage.error(res.msg);
          }
        })
        .catch(() => {
          state.loading = false;
        });
    }
  });
};

// --- Three.js Antigravity Effect (Improved) ---
import * as THREE from 'three';

let scene: THREE.Scene;
let camera: THREE.OrthographicCamera;
let renderer: THREE.WebGLRenderer;
let animationFrameId: number;

// Particle System State
interface Particle {
  x: number;
  y: number;
  vx: number;
  vy: number;
  rotation: number;
  vr: number; // Angular velocity
  scale: number;
  type: number; // 0: Circle, 1: Triangle, 2: Square
  dummy: THREE.Object3D;
}

const particles: Particle[] = [];
const PARTICLE_COUNT = 50; // Sparse like the reference
const COLORS = [0x4285F4, 0x4285F4, 0x4285F4, 0x8AB4F8]; // Mostly Google Blue, some lighter blue

let meshCircle: THREE.InstancedMesh;
let meshTriangle: THREE.InstancedMesh;
let meshSquare: THREE.InstancedMesh;

// Mouse state
const mouse = new THREE.Vector2(0, 0);
const targetMouse = new THREE.Vector2(0, 0);
let isMouseMoved = false;

const initThree = () => {
  if (!canvasRef.value) return;

  // 1. Scene
  scene = new THREE.Scene();
  scene.background = new THREE.Color(0xf2f3f5);

  // 2. Renderer
  renderer = new THREE.WebGLRenderer({
    canvas: canvasRef.value,
    antialias: true,
    alpha: true
  });
  renderer.setPixelRatio(Math.min(window.devicePixelRatio, 2));

  // 3. Camera
  const width = window.innerWidth;
  const height = window.innerHeight;
  camera = new THREE.OrthographicCamera(
    width / -2, width / 2, 
    height / 2, height / -2, 
    1, 1000
  );
  camera.position.z = 100;

  // 4. Create Objects
  createObjects(width, height);

  // 5. Events
  window.addEventListener('resize', resizeCanvas);
  window.addEventListener('mousemove', onMouseMove);
  
  resizeCanvas(); // Trigger initial size set

  // 6. Start Loop
  animate();
};

const createObjects = (width: number, height: number) => {
  // Clear existing
  if (meshCircle) scene.remove(meshCircle);
  if (meshTriangle) scene.remove(meshTriangle);
  if (meshSquare) scene.remove(meshSquare);
  particles.length = 0;

  // Material (Common)
  const material = new THREE.MeshBasicMaterial({
    color: 0x4285F4, // Primary Blue
    transparent: true,
    opacity: 0.8
  });

  // Geometries
  const geoCircle = new THREE.CircleGeometry(12, 32);
  const geoTriangle = new THREE.CircleGeometry(14, 3); // 3 segments = triangle
  const geoSquare = new THREE.PlaneGeometry(20, 20);

  // Instanced Meshes
  // We'll split count roughly by 3
  const countPerType = Math.ceil(PARTICLE_COUNT / 3);

  meshCircle = new THREE.InstancedMesh(geoCircle, material, countPerType);
  meshTriangle = new THREE.InstancedMesh(geoTriangle, material, countPerType);
  meshSquare = new THREE.InstancedMesh(geoSquare, material, countPerType);

  scene.add(meshCircle);
  scene.add(meshTriangle);
  scene.add(meshSquare);

  // Initialize Particles Data
  // Helper to create particle data
  const addParticle = (type: number, index: number, mesh: THREE.InstancedMesh) => {
    const dummy = new THREE.Object3D();
    
    // Random Position
    const x = (Math.random() - 0.5) * width;
    const y = (Math.random() - 0.5) * height;
    
    // Random Color for this instance (if we wanted varied colors, we'd need setColorAt)
    // For now, sticking to Blue/Google Blue theme. 
    // Let's use setColorAt to mix Google colors.
    const colorHex = COLORS[Math.floor(Math.random() * COLORS.length)];
    mesh.setColorAt(index, new THREE.Color(colorHex));

    particles.push({
      x, y,
      vx: (Math.random() - 0.5) * 2, // Slow random drift
      vy: (Math.random() - 0.5) * 2,
      rotation: Math.random() * Math.PI * 2,
      vr: (Math.random() - 0.5) * 0.05, // Slow rotation
      scale: 0.5 + Math.random() * 1.0, // 0.5 to 1.5
      type,
      dummy
    });
  };

  for (let i = 0; i < countPerType; i++) addParticle(0, i, meshCircle);
  for (let i = 0; i < countPerType; i++) addParticle(1, i, meshTriangle);
  for (let i = 0; i < countPerType; i++) addParticle(2, i, meshSquare);
};

const resizeCanvas = () => {
  if (!renderer || !camera) return;
  
  const width = window.innerWidth;
  const height = window.innerHeight;
  
  camera.left = width / -2;
  camera.right = width / 2;
  camera.top = height / 2;
  camera.bottom = height / -2;
  camera.updateProjectionMatrix();
  
  renderer.setSize(width, height);
  
  // Re-distribute particles if they are out of bounds? 
  // Nah, just let them float back or wrap in animate()
};

const onMouseMove = (e: MouseEvent) => {
  isMouseMoved = true;
  // Transform to center-based coordinates
  targetMouse.x = e.clientX - window.innerWidth / 2;
  targetMouse.y = -(e.clientY - window.innerHeight / 2);
};

const animate = () => {
  if (!scene || !camera) return;

  // Smooth mouse
  mouse.lerp(targetMouse, 0.1);

  // Physics Settings
  const FRICTION = 0.96; // Air resistance
  const REPULSION_RADIUS = 250;
  const REPULSION_FORCE = 2.0;
  const BOUNDS_MARGIN = 50;
  
  const width = window.innerWidth;
  const height = window.innerHeight;
  const halfW = width / 2;
  const halfH = height / 2;

  // Update each particle
  let idx0 = 0, idx1 = 0, idx2 = 0;

  particles.forEach(p => {
    // 1. Mouse Repulsion
    const dx = p.x - mouse.x;
    const dy = p.y - mouse.y;
    const distSq = dx*dx + dy*dy;
    
    if (distSq < REPULSION_RADIUS * REPULSION_RADIUS) {
      const dist = Math.sqrt(distSq);
      // Force direction (normalized)
      const fx = dx / dist;
      const fy = dy / dist;
      
      // Strength falls off with distance
      const strength = (1 - dist / REPULSION_RADIUS) * REPULSION_FORCE;
      
      p.vx += fx * strength;
      p.vy += fy * strength;
      
      // Spin them a bit when hit
      p.vr += (Math.random() - 0.5) * 0.02 * strength;
    }

    // 2. Apply Velocity & Friction
    p.x += p.vx;
    p.y += p.vy;
    p.rotation += p.vr;
    
    p.vx *= FRICTION;
    p.vy *= FRICTION;
    p.vr *= FRICTION;

    // 3. Antigravity / Constant Drift
    // Gentle bobbing
    p.vx += Math.sin(performance.now() * 0.001 + p.y * 0.01) * 0.02;
    p.vy += Math.cos(performance.now() * 0.001 + p.x * 0.01) * 0.02;

    // 4. Screen Wrap (Infinite Space)
    if (p.x > halfW + BOUNDS_MARGIN) p.x = -halfW - BOUNDS_MARGIN;
    if (p.x < -halfW - BOUNDS_MARGIN) p.x = halfW + BOUNDS_MARGIN;
    if (p.y > halfH + BOUNDS_MARGIN) p.y = -halfH - BOUNDS_MARGIN;
    if (p.y < -halfH - BOUNDS_MARGIN) p.y = halfH + BOUNDS_MARGIN;

    // 5. Update Matrix
    p.dummy.position.set(p.x, p.y, 0);
    p.dummy.rotation.z = p.rotation;
    p.dummy.scale.set(p.scale, p.scale, 1);
    p.dummy.updateMatrix();

    if (p.type === 0) {
      meshCircle.setMatrixAt(idx0++, p.dummy.matrix);
    } else if (p.type === 1) {
      meshTriangle.setMatrixAt(idx1++, p.dummy.matrix);
    } else {
      meshSquare.setMatrixAt(idx2++, p.dummy.matrix);
    }
  });

  meshCircle.instanceMatrix.needsUpdate = true;
  meshTriangle.instanceMatrix.needsUpdate = true;
  meshSquare.instanceMatrix.needsUpdate = true;

  renderer.render(scene, camera);
  animationFrameId = requestAnimationFrame(animate);
};

</script>

<style lang="less" scoped>
@bg-color: #f2f3f5;
@card-bg: rgba(255, 255, 255, 0.9);
@text-color: #1a1a1a;
@text-muted: #888888;

.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: @bg-color;
  color: @text-color;
  font-family: 'Inter', -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
  overflow: hidden;
  position: relative;
}

.background-canvas {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: 0;
}

.login-content {
  display: flex;
  flex-direction: row;
  align-items: center;
  justify-content: center;
  gap: 8rem;
  z-index: 1;
  max-width: 1400px;
  width: 100%;
  padding: 2rem;
  
  @media (max-width: 1024px) {
    flex-direction: column;
    gap: 4rem;
  }
}

.brand-section {
  flex: 1;
  text-align: left;
  max-width: 500px;
  pointer-events: none;
  
  @media (max-width: 1024px) {
    text-align: center;
    max-width: 100%;
  }

  .brand-title {
    font-size: 5rem;
    font-weight: 900;
    margin-bottom: 1rem;
    color: #000;
    letter-spacing: -0.05em;
    line-height: 0.9;
  }

  .brand-slogan {
    font-size: 1.5rem;
    color: @text-muted;
    font-weight: 300;
    line-height: 1.4;
  }
}

.login-card {
  flex: 0 0 380px;
  background: @card-bg;
  backdrop-filter: blur(20px);
  padding: 3rem;
  border-radius: 24px;
  transition: all 0.3s ease;
  
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.04);
  border: 1px solid rgba(255, 255, 255, 0.6);

  &:hover {
    transform: translateY(-5px);
    box-shadow: 0 30px 60px rgba(0, 0, 0, 0.08);
  }
}

.login-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 2.5rem;

  h2 {
    font-size: 1.5rem;
    font-weight: 700;
    margin: 0;
    letter-spacing: -0.02em;
    color: #000;
  }

  .icon-lang {
    font-size: 1.2rem;
    color: @text-muted;
    cursor: pointer;
    transition: color 0.3s;

    &:hover {
      color: @text-color;
    }
  }
}

.login-form {
  .el-form-item {
    margin-bottom: 1.5rem;
  }
}

:deep(.custom-input) {
  .el-input__wrapper {
    background: #f5f5f5;
    box-shadow: none;
    border: 1px solid transparent;
    border-radius: 12px;
    padding: 8px 12px;
    transition: all 0.2s ease;

    &:hover {
      background: #eeeeee;
    }
    
    &.is-focus {
      background: #fff;
      border-color: #000;
      box-shadow: 0 0 0 2px rgba(0,0,0,0.05);
    }
  }

  .el-input__inner {
    color: @text-color;
    height: 40px;
    font-size: 1rem;
    font-weight: 500;
    
    &::placeholder {
      color: #999;
      font-weight: 400;
    }
  }

  .input-icon {
    font-size: 1.1rem;
    color: @text-muted;
  }
}

.login-btn {
  width: 100%;
  height: 52px;
  border-radius: 14px;
  font-size: 1rem;
  font-weight: 600;
  background: #000;
  border: none;
  color: #fff;
  margin-top: 1.5rem;
  transition: all 0.2s ease;
  letter-spacing: 0.02em;

  &:hover {
    transform: scale(1.01);
    background: #222;
    box-shadow: 0 10px 20px rgba(0, 0, 0, 0.1);
  }
  
  &:active {
    transform: scale(0.98);
  }
}

.login-footer {
  margin-top: 2rem;
  text-align: center;
  font-size: 0.8rem;
  color: #aaa;
}
</style>

<template>
  <a
    class="collections-grid-item cursor-pointer link-style"
    @click.prevent="goDetail"
  >
    <div class="collections-grid-item-header flex text-sm items-center gap-2">
      <svg
        xmlns="http://www.w3.org/2000/svg"
        width="20"
        height="20"
        viewBox="0 0 20 20"
      >
        <path
          fill="currentColor"
          d="M7 13h4v4H7zM12 5h4v4h-4zM12 10h5v5h-5zM3 4h8v8H3z"
        ></path>
      </svg>
      {{ name }}
    </div>
    <div class="text-gray-500 text-xs">{{ type }}</div>
  </a>
</template>
<script lang="ts">
import { defineComponent, reactive, toRefs } from 'vue';
import { useRouter } from 'vue-router';

export default defineComponent({
  props: {
    id: {
      type: String,
      required: true,
    },
    name: {
      type: String,
      required: true,
    },
    type: {
      type: String,
      required: true,
    },
  },
  setup(props) {
    const router = useRouter();

    const methods = reactive({
      goDetail() {
        router.push({
          path: '/image/c/' + props.id,
        });
      },
    });

    return {
      ...toRefs(methods),
    };
  },
});
</script>

<style scoped>
.collections-grid-item {
  background-color: var(--b20);
  border-radius: 8px;
  display: flex;
  flex-direction: column;
  height: 100%;
  justify-content: space-between;
  padding: 20px;
}
.link-style,
a {
  color: var(--b100);
  cursor: pointer;
  text-decoration: none;
  transition: color 0.2s ease;
}
</style>

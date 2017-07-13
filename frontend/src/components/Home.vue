<template>
  <div>
    <h1>Home</h1>
    Go to <router-link :to="{name: 'trades', query: {sim: 'local', day: 0, agents: 'consumers,firms', step: 1}}">demo trades</router-link>
  </div>
</template>

<script>
export default {
  name: 'home',
};
</script>

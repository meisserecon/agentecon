<template>
  <div>
    <h1>Tradegraph</h1>
    <p>Data: {{ data }}</p>
  </div>
</template>

<script>
export default {
  name: 'tradegraph',
  props: ['data'],
};
</script>

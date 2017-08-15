<template>
  <div id="app">
    <ul class="homenav">
      <li><router-link :to="{name: 'home'}">Simulations</router-link></li>
    </ul>

    <router-view></router-view>
  </div>
</template>

<script>
export default {
  name: 'app',
};
</script>

<style lang="sass">
@import 'assets/sass/vars'
@import 'assets/sass/mixins'

body
  margin: 20px

h1, h2, h3
  text-align: left

h1
  font: bold 40px/1 $avenir
  text-align: left

h2
  font: 500 14px/1.4 $avenir

#app
  padding: 25px 50px
  font-family: 'Avenir', Helvetica, Arial, sans-serif
  -webkit-font-smoothing: antialiased
  -moz-osx-font-smoothing: grayscale
  text-align: center
  color: #111

.nolist
  padding: 0
  margin: 0
  list-style-type: none

.homenav
  padding: 0
  margin: 0
  list-style-type: none

  a
    color: $blue
    text-decoration: none
    &:hover
      color: $blue-hover

  li
    text-align: left

.context
  position: absolute
  left: -1000em
  top: 0
  z-index: 20
  display: inline-block
  max-width: 500px
  padding: 10px
  box-shadow: 0 0 4px rgba(0,0,0,.2)
  border-radius: 7px
  background-color: white
  opacity: 0
  transition: opacity .2s
  text-align: left
  &.in
    opacity: 1

.btn
  display: inline-block
  font-weight: 400
  line-height: 1.25
  text-align: center
  white-space: nowrap
  vertical-align: middle
  user-select: none
  border: 1px solid transparent
  padding: .5rem 1rem
  border-radius: .25rem
  transition: all .2s ease-in-out
  color: #292b2c
  background-color: #fff
  border-color: #ccc
  cursor: pointer
  outline: 0
  &:hover
    color: #292b2c
    background-color: #e6e6e6
    border-color: #adadad
  &:focus
    box-shadow: 0 0 0 2px rgba(204,204,204,.5)

.form-control
  padding: .5rem .75rem
  font-size: 1rem
  line-height: 1.25
  color: #464a4c
  background-color: #fff
  background-image: none
  background-clip: padding-box
  border: 1px solid rgba(0,0,0,.15)
  border-radius: .25rem
  transition: border-color ease-in-out .15s,box-shadow ease-in-out .15s
  &:focus
    color: #464a4c
    background-color: #fff
    border-color: #5cb3fd
    outline: 0

.linklist
  padding: 0
  margin: 0
  list-style-type: none

  li
    text-align: left

  a
    +link-styling
</style>

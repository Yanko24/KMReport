<!--<template>-->
<!--  <div class="login" clearfix>-->
<!--    <div class="login-wrap">-->
<!--      <el-row type="flex" justify="center">-->
<!--        <el-form ref="loginForm" :model="user" :rules="rules" status-icon label-width="80px">-->
<!--          <h3>KMReport</h3>-->
<!--          <hr>-->
<!--          <el-form-item prop="username" label="用户名">-->
<!--            <el-input v-model="user.username" placeholder="请输入用户名" prefix-icon></el-input>-->
<!--          </el-form-item>-->
<!--          <el-form-item id="password" prop="password" label="密码">-->
<!--            <el-input v-model="user.password" show-password placeholder="请输入密码"></el-input>-->
<!--          </el-form-item>-->
<!--          <router-link to="/">找回密码</router-link>-->
<!--          <router-link to="/register">注册账号</router-link>-->
<!--          <el-form-item>-->
<!--            <el-button type="primary" icon="el-icon-upload" @click="doLogin()">登 录</el-button>-->
<!--          </el-form-item>-->
<!--        </el-form>-->
<!--      </el-row>-->
<!--    </div>-->
<!--  </div>-->
<!--</template>-->

<!--<script>-->
<!--import axios from "axios";-->
<!--export default {-->
<!--  name: "login",-->
<!--  data() {-->
<!--    return {-->
<!--      user: {-->
<!--        username: "",-->
<!--        password: ""-->
<!--      }-->
<!--    };-->
<!--  },-->
<!--  created() {-->
<!--  },-->
<!--  methods: {-->
<!--    doLogin() {-->
<!--      if (!this.user.username) {-->
<!--        this.$message.error("请输入用户名！");-->
<!--      } else if (!this.user.password) {-->
<!--        this.$message.error("请输入密码！");-->
<!--      } else {-->
<!--        axios-->
<!--            .post("/login/", {-->
<!--              name: this.user.username,-->
<!--              password: this.user.password-->
<!--            })-->
<!--            .then(res => {-->
<!--              if (res.data.status === 200) {-->
<!--                this.$router.push({ path: "/personal" });-->
<!--              } else {-->
<!--                alert("您输入的用户名或密码错误！");-->
<!--              }-->
<!--            });-->
<!--      }-->
<!--    }-->
<!--  }-->
<!--};-->
<!--</script>-->
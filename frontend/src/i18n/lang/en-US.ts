export default {
  ui: {
    app: {
      //产品信息
      name: "Zenith",
      productName: "ZENITH FLOW",
      productNameMini: "ZF",
      copyright: "zenithflow.com"
    },
    setting: {
      // 右侧可视化设置窗口
      pageTitle: "Theme settings",
      //
      sidebarThemeDark: "Dark sidebar",
      sidebarThemeLight: "Light sidebar",
      topHeaderThemeDark: "Dark top bar",
      topHeaderThemeLight: "Light top bar",
      topHeaderThemePrimary: "Theme color top bar",
      //
      colorTheme1: "default",
      colorTheme2: "cyan",
      colorTheme3: "blue",
      colorTheme4: "green",
      colorTheme5: "turquoise",
      colorTheme6: "indigo",
      colorTheme7: "brown",
      colorTheme8: "purple",
      colorTheme9: "gray",
      colorTheme10: "orange",
      colorTheme11: "pink",
      colorTheme12: "yellow",
      colorTheme13: "red",
      colorTheme14: "dark",
      //
      title2: "Navigation mode",
      sidebarLayoutLeft: "Left menu layout",
      sidebarLayoutTop: "Top menu layout",
      sidebarLayoutMix: "Mixed menu layout",
      contentFull: "The content area is filled",
      //
      title3: "Other configuration",
      logoAuto: "Fixed Logo bar",
      colorIcon: "Sidebar displays colored icons",
      sidebarUniOpened: "Sidebar unique opened",
      openTabsPage: "Enable tab",
      tabStyles: "Tabs display style",
      tabStyles1: "default",
      tabStyles2: "Dots",
      tabStyles3: "card",
      //
      settingTips:
        "This function can preview various layout effects in real time. More complete configurations can be set in src/constants/config.ts. The configuration will be remembered after modification and can be used in a production environment.",
      copyBtn: "Copy settings",
      copySuccess: "Copied successfully"
    },
    router: {
      //路由
      pageWorkbench: "Dashboard",
      pageHome: "Home",
      pageLogin: "Login",
      pageError: "ErrorPage",
      moreMenus: "More",
      tabs: {
        //tab标签页
        closeThis: "Close current tab",
        closeOther: "Close other tabs",
        closeAll: "Close all tabs",
        closeRight: "Close right",
        closeLeft: "Close left",
        closeOnlyOneTips: "Only one tab left, does not support closing"
      },
      error: {
        backBtn: "Back",
        homeBtn: "Home",
        404: {
          //404
          title: "404",
          des: "The visited page does not exist"
        },
        error: {
          //error
          title: "Error",
          des: "Access error"
        }
      }
    },
    user: {
      //用户模块
      links: {
        userCenter: "Personal center",
        editPassword: "Change password",
        logout: "Sign out"
      },
      message: {
        notice: "Notice",
        upcoming: "Upcoming"
      }
    },
    widget: {
      //通用小组件
      selectTips: "Please choose"
    },
    login: {
      // 登录页
      loginOk: "Login successful",
      userNamePlaceholder: "Username",
      passwordPlaceholder: "Password",
      captchaPlaceholder: "Verification Code",
      remember: "Remember password",
      loginBtn: "Sign in",
      rules: {
        userName: "Required field cannot be empty",
        password: "Required field cannot be empty",
        captcha: "Required field cannot be empty"
      }
    }
  }
};

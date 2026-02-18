import {defineConfig} from 'vitepress'

// https://vitepress.dev/reference/site-config
export default defineConfig({
  srcDir: "pages",
  base: "/Khat",

  title: "Khat",
  description: "Chat To Discord, Discord To Chat",
  themeConfig: {
    // https://vitepress.dev/reference/default-theme-config
    nav: [
      {text: 'Home', link: '/'},
      {text: 'Usage', link: '/users/getting-started'},
      {text: 'Development', link: '/development'},
    ],

    search: {
      provider: 'local'
    },

    editLink: {
      pattern: 'https://github.com/Tywrap-Studios/Khat/edit/master/docs/pages/:path'
    },

    externalLinkIcon: true,

    sidebar: {
      'users/': [{
        text: 'Usage',
        items: [
          {text: 'Getting Started', link: '/users/getting-started'},
          {
            text: 'Configuration', items: [
              {text: 'Global', link: '/users/config/global'},
              {text: 'Per-webhook', link: '/users/config/webhook'},
              {text: 'Versions & Migration', link: '/users/config/versions'},
            ],
            collapsed: false
          }
        ]
      }],
      'development/': [{
        text: 'Development',
        items: [
          {text: 'Introduction', link: '/development'},
          {text: 'Changelog', link: '/development/changelog'},
          {
            text: 'Internals', items: [
              {text: 'mRPC', link: '/development/mrpc'},
              {text: 'krapher', link: '/development/krapher'},
            ],
          },
        ]
      }]
    },

    socialLinks: [
      {icon: 'github', link: 'https://github.com/Tywrap-Studios/Khat'},
      {icon: 'youtube', link: 'https://www.youtube.com/channel/UCjdRI_nlvxTw4W2Ldfsf5EA'},
      {icon: 'discord', link: 'https://tiazzz.me/discord'},
    ],
  },

  cleanUrls: true,
  lastUpdated: true,
  ignoreDeadLinks: true,
})

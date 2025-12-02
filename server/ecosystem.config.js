module.exports = {
  apps : [{
    name: "api-agente-de-reparacion",
    script: "/home/user/Agente-de-Reparacion/server/dist/main.js",
    cwd: "/home/user/Agente-de-Reparacion/server",
    instances: 1,
    autorestart: true,
    watch: false,
    max_memory_restart: '256M',
    env: {
      NODE_ENV: "production",
    }
  }]
}
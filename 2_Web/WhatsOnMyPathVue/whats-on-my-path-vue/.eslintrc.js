module.exports = {
  root: true,
  env: {
    node: true,
  },
  extends: [
    "plugin:vue/vue3-essential",
    "eslint:recommended",
    "plugin:prettier/recommended",
  ],
  parserOptions: {
    parser: "@babel/eslint-parser",
  },
  rules: {
    "no-console": process.env.NODE_ENV === "production" ? "warn" : "off",
    "no-debugger": process.env.NODE_ENV === "production" ? "warn" : "off",
    "no-unused-vars": "off",
    "no-undef": "off",
    "no-useless-escape": "off",
    "no-sparse-arrays": "off",
    "no-cond-assign": "off",
    "no-func-assign": "off",
    "no-empty": "off",
    "no-prototype-builtins": "off",
    "getter-return": "off",
    "no-redeclare": "off",
    "no-self-assign": "off",
    "import/no-unused-modules": [1, { unusedExports: true }],
  },
};

group "default" {
  targets = [
    "gateway",
    "order",
    "stock",
    "payment",
  ]
}

target "gateway" {
  context    = "./gateway-service"
  dockerfile = "./Dockerfile"
  tags       = ["stepanovdeveloper/shop-platform:gateway-service"]
  platforms  = ["linux/amd64", "linux/arm64"]
}

target "order" {
  context    = "./order-service"
  dockerfile = "./Dockerfile"
  tags       = ["stepanovdeveloper/shop-platform:order-service"]
  platforms  = ["linux/amd64", "linux/arm64"]
}

target "stock" {
  context    = "./stock-service"
  dockerfile = "./Dockerfile"
  tags       = ["stepanovdeveloper/shop-platform:stock-service"]
  platforms  = ["linux/amd64", "linux/arm64"]
}

target "payment" {
  context    = "./payment-service"
  dockerfile = "./Dockerfile"
  tags       = ["stepanovdeveloper/shop-platform:payment-service"]
  platforms  = ["linux/amd64", "linux/arm64"]
}

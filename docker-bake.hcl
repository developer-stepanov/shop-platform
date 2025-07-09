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
  tags       = ["developerstepanov/shop-platform:gateway-service-1.0.0"]
  platforms  = ["linux/amd64", "linux/arm64"]
}

target "order" {
  context    = "./order-service"
  dockerfile = "./Dockerfile"
  tags       = ["developerstepanov/shop-platform:order-service-1.0.0"]
  platforms  = ["linux/amd64", "linux/arm64"]
}

target "stock" {
  context    = "./stock-service"
  dockerfile = "./Dockerfile"
  tags       = ["developerstepanov/shop-platform:stock-service-1.0.0"]
  platforms  = ["linux/amd64", "linux/arm64"]
}

target "payment" {
  context    = "./payment-service"
  dockerfile = "./Dockerfile"
  tags       = ["developerstepanov/shop-platform:payment-service-1.0.0"]
  platforms  = ["linux/amd64", "linux/arm64"]
}

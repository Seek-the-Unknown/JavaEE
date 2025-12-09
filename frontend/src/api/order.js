import request from "@/utils/request";

export function createOrder(data) {
  return request({ url: "/orders", method: "post", data });
}
// [新增] 批量预约
export function createBatchOrders(orders) {
  return request({ url: "/orders/batch", method: "post", data: orders });
}
export function getOrdersByUserId(userId) {
  return request({ url: `/orders/user/${userId}`, method: "get" });
}
export function getBookedSlots(venueId, date) {
  return request({
    url: `/orders/venue/${venueId}`,
    method: "get",
    params: { date },
  });
}
export function cancelOrder(orderId) {
  return request({ url: `/orders/${orderId}/cancel`, method: "put" });
}

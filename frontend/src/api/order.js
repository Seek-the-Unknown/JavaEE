import request from "@/utils/request";

/**
 * 创建新订单
 * @param {object} data - 包含 userId, venueId, bookingDate, startTime, endTime 等
 */
export function createOrder(data) {
  return request({
    url: "/orders",
    method: "post",
    data,
  });
}

/**
 * 根据用户ID获取订单列表
 * @param {number} userId - 用户ID
 */
export function getOrdersByUserId(userId) {
  return request({
    url: `/orders/user/${userId}`,
    method: "get",
  });
}

/**
 * ======== 新增函数 ========
 * 获取场馆某日的已预约时段
 * @param {number} venueId - 场馆ID
 * @param {string} date - 日期字符串 'YYYY-MM-DD'
 */
export function getBookedSlots(venueId, date) {
  return request({
    url: `/orders/venue/${venueId}`,
    method: "get",
    params: { date },
  });
}
export function cancelOrder(orderId) {
  return request({
    url: `/orders/${orderId}/cancel`,
    method: "put",
  });
}

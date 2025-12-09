// 导入我们封装好的axios实例
import request from "@/utils/request";

// 定义一个函数，用于获取场馆列表
export function getVenueList(query) {
  return request({
    url: "/venues",
    method: "get",
    params: { name: query },
  });
}

// 定义一个函数，用于根据ID获取场馆详情
export function getVenueById(id) {
  return request({
    url: `/venues/${id}`,
    method: "get",
  });
}

/**
 * 新增一个场馆
 * @param {object} data - 场馆信息
 */
export function createVenue(data) {
  return request({
    url: "/venues",
    method: "post",
    data,
  });
}

/**
 * 更新一个场馆
 * @param {number} id - 场馆ID
 * @param {object} data - 场馆信息
 */
export function updateVenue(id, data) {
  return request({
    url: `/venues/${id}`,
    method: "put",
    data,
  });
}

/**
 * 删除一个场馆
 * @param {number} id - 场馆ID
 */
export function deleteVenue(id) {
  return request({
    url: `/venues/${id}`,
    method: "delete",
  });
}

/**
 * ======== 新增函数 ========
 * 上传场馆图片
 * @param {File} file - 图片文件
 */
export function uploadImage(file) {
  const formData = new FormData();
  formData.append("file", file);

  return request({
    url: "/files/upload",
    method: "post",
    data: formData,
    headers: {
      "Content-Type": "multipart/form-data",
    },
  });
}

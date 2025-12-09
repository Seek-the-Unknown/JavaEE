import request from "@/utils/request";

export function getVenueList(name) {
  return request({ url: "/venues", method: "get", params: { name } });
}
// [新增]
export function getMyVenues() {
  return request({ url: "/venues/my", method: "get" });
}
export function getVenueById(id) {
  return request({ url: `/venues/${id}`, method: "get" });
}
export function createVenue(data) {
  return request({ url: "/venues", method: "post", data });
}
export function updateVenue(id, data) {
  return request({ url: "/venues", method: "put", data: { id, ...data } });
}
export function deleteVenue(id) {
  return request({ url: `/venues/${id}`, method: "delete" });
}
export function uploadImage(file) {
  const formData = new FormData();
  formData.append("file", file);
  return request({
    url: "/upload",
    method: "post",
    data: formData,
    headers: { "Content-Type": "multipart/form-data" },
  });
}

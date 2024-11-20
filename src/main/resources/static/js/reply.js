
// async function getList(replyId) {
//
//     const result = await axios.get(`/replies/${replyId}`, {params: {page,size}})
//     if(goLast){
//         console.log(result.data)
//         return result.data;
//     }
// }
// 댓글 목록 가져오기

async function getList(replyId) {
    console.log(`댓글 목록 요청: /replies/${postId}`);
    const response = await axios.get(`/replies/${postId}`);
    console.log('댓글 목록 응답:', response.data);
    return response.data;
}

//댓글 등록
async function addReply(replyObj, postId) {
    console.log('댓글 등록 요청:', replyObj);

    const response = await axios.post(`/replies/${postId}`, replyObj)
    console.log('댓글 등록 성공:', response.data);

    return response.data
}

//
// async function addReReply(replyObj, parentId) {
//
//     const response = await axios.post(`/replies/${parentId}`, replyObj);
//     return response.data;
//
// }

async function getReply(replyId) {

    const response = await axios.get(`/replies/${replyId}`);
    return response.data;

}
//수정
async function modifyReply(replyId, replyObj) {
    const response = await axios.put(`/replies/${replyId}`, replyObj);
    return response.data;
}

//삭제
async function removeReply(replyId) {

    const response = await axios.delete(`/replies/${replyId}`);
    return response.data;
}
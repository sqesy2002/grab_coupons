<template>
    <div class="coupon-info-container">
        <!-- 操作区域 -->
        <div class="actions-bar">
            <button class="btn btn-primary" @click="handleCreateCoupon">创建优惠券</button>
        </div>

        <!-- 加载提示 -->
        <div v-if="loading" class="loading-container">
            <div class="spinner"></div>
            <p>正在加载数据...</p>
        </div>

        <!-- 表格与分页容器 -->
        <div v-else class="table-container">
            <!-- 表格区域 -->
            <table class="custom-table">
                <thead>
                <tr>
                    <th>券ID</th>
                    <th>券名称</th>
                    <th>描述</th>
                    <th>每人限领</th>
                    <th>有效期(天)</th>
                    <th>发布时间</th>
                    <th>下架时间</th>
                </tr>
                </thead>
                <tbody>
                <tr v-if="tableData.length === 0">
                    <td :colspan="7" class="no-data">暂无数据</td>
                </tr>
                <tr v-for="coupon in tableData" :key="coupon.couponId">
                    <td>{{ coupon.couponId }}</td>
                    <td>{{ coupon.couponName }}</td>
                    <td class="description-cell">{{ coupon.description }}</td>
                    <td>{{ coupon.maxPerUser }}</td>
                    <td>{{ coupon.validityDuration }}</td>
                    <td>{{ formatDateTime(coupon.valid_time) }}</td>
                    <td>{{ formatDateTime(coupon.invalid_time) }}</td>
                </tr>
                </tbody>
            </table>

            <!-- 分页区域 -->
            <div class="pagination-container">
                <button class="btn" @click="goToPrevPage" :disabled="pagination.pageNum <= 1">
                    上一页
                </button>
                <span>第 {{ pagination.pageNum }} 页 / 共 {{ totalPages }} 页</span>
                <button class="btn" @click="goToNextPage" :disabled="pagination.pageNum >= totalPages">
                    下一页
                </button>
            </div>
        </div>

        <!-- 创建优惠券的弹窗 -->
        <div v-if="showCreateModal" class="modal-overlay">
            <div class="modal-content">
                <h3>创建新优惠券</h3>
                <div class="modal-body">
                    <div class="form-group">
                        <label for="couponName">券名称:</label>
                        <input type="text" id="couponName" v-model="newCoupon.couponName">
                    </div>
                    <div class="form-group">
                        <label for="description">描述:</label>
                        <textarea id="description" v-model="newCoupon.description"></textarea>
                    </div>
                    <div class="form-group">
                        <label for="maxPerUser">每人限领:</label>
                        <input type="number" id="maxPerUser" v-model.number="newCoupon.maxPerUser" min="1">
                    </div>
                    <div class="form-group">
                        <label for="validityDuration">有效期(天):</label>
                        <input type="number" id="validityDuration" v-model.number="newCoupon.validityDuration" min="1">
                    </div>
                    <div class="form-group">
                        <label for="valid_time">可领取时间:</label>
                        <input type="datetime-local" id="valid_time" v-model="newCoupon.valid_time">
                    </div>
                    <div class="form-group">
                        <label for="invalid_time">下架时间:</label>
                        <input type="datetime-local" id="invalid_time" v-model="newCoupon.invalid_time">
                    </div>
                </div>
                <div class="modal-footer">
                    <button class="btn" @click="showCreateModal = false">取消</button>
                    <button class="btn btn-primary" @click="submitCreateCoupon">创建</button>
                </div>
            </div>
        </div>

    </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue';
import axios from 'axios';

// 表格加载状态
const loading = ref(true);
// 表格数据
const tableData = ref([]);
// 分页信息
const pagination = reactive({
    pageNum: 1,
    pageSize: 10,
    total: 0,
});

const showCreateModal = ref(false);  // 创建优惠券的弹窗

const getDefaultNewCoupon = () => ({
    couponName: '',
    description: '',
    maxPerUser: 1,
    validityDuration: 30,
    valid_time: '',
    invalid_time: ''
});

const newCoupon = reactive(getDefaultNewCoupon());

// 计算总页数
const totalPages = computed(() => {
    if (pagination.total === 0) return 1;
    return Math.ceil(pagination.total / pagination.pageSize);
});

// 获取优惠券数据的方法
const fetchData = async () => {
    loading.value = true;
    try {
        const response = await axios.get('/api/coupon/page', {
            params: {
                pageNum: pagination.pageNum,
                pageSize: pagination.pageSize,
            }
        });

        console.log('优惠券页面 - 后端返回数据:', response.data);

        if (response.data && response.data.code === '200' && response.data.data) {
            tableData.value = response.data.data.list || [];
            pagination.total = response.data.data.total || 0;
        } else {
            const errorMessage = response.data.msg || '后端返回的数据格式不符合预期';
            tableData.value = [];
            pagination.total = 0;
            console.error('获取优惠券数据失败:', errorMessage, response.data);
            alert(`获取优惠券数据失败: ${errorMessage}`);
        }
    } catch (error) {
        console.error("请求优惠券API时出错:", error);
        alert('请求优惠券API失败，请检查网络连接或浏览器控制台');
    } finally {
        loading.value = false;
    }
};

// 组件挂载时获取初始数据
onMounted(() => {
    fetchData();
});

// 翻页方法
const goToPrevPage = () => {
    if (pagination.pageNum > 1) {
        pagination.pageNum--;
        fetchData();
    }
};

const goToNextPage = () => {
    if (pagination.pageNum < totalPages.value) {
        pagination.pageNum++;
        fetchData();
    }
};


// 格式化日期时间
const formatDateTime = (cellValue) => {
    if (!cellValue) return 'N/A';
    if (Array.isArray(cellValue)) {
        const [year, month, day, hour, minute, second] = cellValue;
        return `${year}-${String(month).padStart(2, '0')}-${String(day).padStart(2, '0')} ${String(hour).padStart(2, '0')}:${String(minute).padStart(2, '0')}:${String(second).padStart(2, '0')}`;
    }
    return new Date(cellValue).toLocaleString('zh-CN', { hour12: false }).replace(/\//g, '-');
};

// “创建优惠券”按钮点击事件
const handleCreateCoupon = () => {
    // 重置表单为默认值
    Object.assign(newCoupon, getDefaultNewCoupon());
    // 显示弹窗
    showCreateModal.value = true;
};

// 提交创建优惠券的表单
const submitCreateCoupon = async () => {
    // 基础校验
    if (!newCoupon.couponName.trim()) {
        alert('优惠券名称不能为空！');
        return;
    }
    if (!newCoupon.valid_time || !newCoupon.invalid_time) {
        alert('请选择有效的起止时间！');
        return;
    }

    try {
        // 后端需要 @RequestBody，所以直接将 newCoupon 对象作为 post 的第二个参数
        const response = await axios.post('/api/coupon/add', newCoupon);

        if (response.data && response.data.code === '200') {
            alert(response.data.msg || '优惠券创建成功！');
            showCreateModal.value = false; // 关闭弹窗
            fetchData(); // 刷新列表
        } else {
            alert(`创建失败: ${response.data.msg || '未知错误'}`);
        }
    } catch (error) {
        console.error("创建优惠券时出错:", error);
        alert('创建优惠券请求失败！');
    }
};

</script>

<style scoped>
.coupon-info-container {
    padding: 20px;
    background-color: #f9f9f9;
    border-radius: 8px;
}

.actions-bar {
    margin-bottom: 20px;
    display: flex;
    gap: 10px;
}

.btn {
    padding: 8px 16px;
    border: 1px solid #ccc;
    border-radius: 4px;
    background-color: #fff;
    cursor: pointer;
    font-size: 14px;
    transition: all 0.3s ease;
}

.btn:hover {
    border-color: #999;
    background-color: #f0f0f0;
}

.btn:disabled {
    cursor: not-allowed;
    opacity: 0.5;
}

.btn-primary {
    background-color: #007bff;
    color: white;
    border-color: #007bff;
}

.btn-primary:hover {
    background-color: #0056b3;
    border-color: #0056b3;
}

.loading-container {
    text-align: center;
    padding: 50px;
    color: #666;
}
.spinner {
    width: 40px;
    height: 40px;
    border: 4px solid #f3f3f3;
    border-top: 4px solid #3498db;
    border-radius: 50%;
    animation: spin 1s linear infinite;
    margin: 0 auto 10px;
}
@keyframes spin {
    0% { transform: rotate(0deg); }
    100% { transform: rotate(360deg); }
}

.table-container {
    background-color: #fff;
    border-radius: 8px;
    box-shadow: 0 2px 8px rgba(0,0,0,0.05);
    overflow: hidden;
}

.custom-table {
    width: 100%;
    border-collapse: collapse;
}

.custom-table th,
.custom-table td {
    padding: 12px 15px;
    text-align: left;
    border-bottom: 1px solid #e0e0e0;
}

.custom-table thead th {
    background-color: #f5f7fa;
    font-weight: 600;
    color: #333;
}

.custom-table tbody tr:hover {
    background-color: #f0f8ff;
}

.custom-table .no-data {
    text-align: center;
    color: #999;
    padding: 30px;
}

/* 针对描述列，防止内容过长破坏布局 */
.description-cell {
    max-width: 300px;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
}
.description-cell:hover {
    white-space: normal;
    overflow: visible;
}


.pagination-container {
    display: flex;
    justify-content: flex-end;
    align-items: center;
    padding: 15px;
    gap: 15px;
}

/* 弹窗和表单的样式 */
.modal-overlay {
    position: fixed;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background-color: rgba(0, 0, 0, 0.5);
    display: flex;
    justify-content: center;
    align-items: center;
    z-index: 1000;
}

.modal-content {
    background-color: white;
    padding: 20px 30px;
    border-radius: 8px;
    box-shadow: 0 4px 15px rgba(0,0,0,0.2);
    width: 500px;
}

.modal-content h3 {
    margin-top: 0;
    border-bottom: 1px solid #eee;
    padding-bottom: 15px;
    margin-bottom: 20px;
}

.modal-body {
    display: flex;
    flex-direction: column;
    gap: 15px;
}

.form-group {
    display: flex;
    flex-direction: column;
    gap: 5px;
}

.form-group label {
    font-weight: 600;
    font-size: 14px;
}

.form-group input,
.form-group textarea {
    padding: 10px;
    border: 1px solid #ccc;
    border-radius: 4px;
    font-size: 14px;
    width: 100%;
    box-sizing: border-box; /* 让 padding 不会撑大宽度 */
}

.form-group textarea {
    resize: vertical;
    min-height: 80px;
}

.modal-footer {
    display: flex;
    justify-content: flex-end;
    gap: 10px;
    border-top: 1px solid #eee;
    padding-top: 20px;
    margin-top: 20px;
}
</style>
<template>
    <div class="user-info-container">
        <!-- 操作区域 -->
        <div class="actions-bar">
            <button class="btn btn-primary" @click="handleGenerateUsers">生成用户</button>
            <button class="btn btn-danger" @click="handleDeleteAllUsers">删除全部用户</button>
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
                    <th>用户ID</th>
                    <th>昵称</th>
                    <th>姓名</th>
                    <th>手机号</th>
                    <th>状态</th>
                    <th>创建时间</th>
                    <th>更新时间</th>
                </tr>
                </thead>
                <tbody>
                <tr v-if="tableData.length === 0">
                    <td :colspan="7" class="no-data">暂无数据</td>
                </tr>
                <tr v-for="user in tableData" :key="user.userId">
                    <td>{{ user.userId }}</td>
                    <td>{{ user.nickname }}</td>
                    <td>{{ user.name }}</td>
                    <td>{{ user.phoneNumber }}</td>
                    <td>
                        <span :class="['status-badge', `status-${user.status}`]">{{ user.status }}</span>
                    </td>
                    <td>{{ formatDateTime(user.insertTime) }}</td>
                    <td>{{ formatDateTime(user.updateTime) }}</td>
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

        <div v-if="showGenerateModal" class="modal-overlay">
            <div class="modal-content">
                <h3>生成用户</h3>
                <div class="modal-body">
                    <label for="user-quantity">请输入生成数量：</label>
                    <input type="number" id="user-quantity" class="modal-input" v-model="generateQuantity" min="1">
                </div>
                <div class="modal-footer">
                    <button class="btn" @click="showGenerateModal = false">取消</button>
                    <button class="btn btn-primary" @click="confirmGenerate">确定生成</button>
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

// 计算总页数
const totalPages = computed(() => {
    if (pagination.total === 0) return 1;
    return Math.ceil(pagination.total / pagination.pageSize);
});

// 控制生成用户弹窗的显示/隐藏
const showGenerateModal = ref(false);
// 绑定生成用户的数量，默认为 10
const generateQuantity = ref(1000);

// 获取用户数据的方法
const fetchData = async () => {
    loading.value = true;
    try {
        const response = await axios.get('/api/user/page', {
            params: {
                pageNum: pagination.pageNum,
                pageSize: pagination.pageSize,
            }
        });

        console.log('后端返回的原始数据:', response.data);

        // 检查 code 是否为 "200"
        if (response.data && response.data.code === '200' && response.data.data) {
            tableData.value = response.data.data.list || []; // 如果list不存在则给一个空数组
            pagination.total = response.data.data.total || 0;
        } else {
            // 如果code不为 "200" 或数据格式不对，则显示后端的 msg 信息
            const errorMessage = response.data.msg || '后端返回的数据格式不符合预期';
            tableData.value = [];
            pagination.total = 0;
            console.error('获取用户数据失败:', errorMessage, response.data);
            alert(`获取用户数据失败: ${errorMessage}`);
        }
    } catch (error) {
        console.error("请求后端API时出错:", error);
        alert('请求后端API失败，请检查网络连接或浏览器控制台');
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
    if (!cellValue) return '';
    if (Array.isArray(cellValue)) {
        const [year, month, day, hour, minute, second] = cellValue;
        return `${year}-${String(month).padStart(2, '0')}-${String(day).padStart(2, '0')} ${String(hour).padStart(2, '0')}:${String(minute).padStart(2, '0')}:${String(second).padStart(2, '0')}`;
    }
    // 兼容标准ISO字符串
    return new Date(cellValue).toLocaleString('zh-CN', { hour12: false }).replace(/\//g, '-');
};

// “生成用户”按钮点击事件
const handleGenerateUsers = () => {
    // 将之前的 alert 功能替换为显示弹窗
    showGenerateModal.value = true;
};

// 处理弹窗中“确定生成”的逻辑 ---
const confirmGenerate = async () => {
    // 输入验证
    if (!generateQuantity.value || generateQuantity.value < 1) {
        alert('请输入一个有效的正整数！');
        return;
    }
    // 关闭弹窗
    showGenerateModal.value = false;
    // 调用后端接口
    try {
        // 后端函数头是 @RequestParam，通常对应 GET 或 POST form-data
        // 这里我们使用 POST，并将参数放在 params 中，axios会将其作为URL查询参数
        const response = await axios.get('/api/user/generate', {
            params: {
                user_quantity: generateQuantity.value
            }
        });

        if (response.data && response.data.code === '200') {
            alert(response.data.msg || '用户生成成功！');
            // 成功后刷新列表
            fetchData();
        } else {
            alert(`生成失败: ${response.data.msg || '未知错误'}`);
        }
    } catch (error) {
        console.error("生成用户时出错:", error);
        alert('生成用户请求失败！');
    }
};

// “删除全部用户”按钮点击事件 ---
const handleDeleteAllUsers = () => {
    // 使用浏览器原生的确认框
    if (window.confirm('此操作将永久删除所有用户，是否继续？')) {
        // 确认后，调用删除接口
        axios.get('/api/user/deleteAll')
            .then(response => {
                if (response.data && response.data.code === '200') {
                    alert(response.data.msg || '全部用户删除成功！');
                    // 删除成功后，刷新表格数据
                    fetchData();
                } else {
                    alert(`删除失败: ${response.data.msg || '未知错误'}`);
                }
            })
            .catch(error => {
                console.error("删除全部用户时出错:", error);
                alert('删除全部用户请求失败！');
            });
    } else {
        console.log('已取消删除操作');
    }
};
</script>

<style scoped>
/* 整体容器 */
.user-info-container {
    padding: 20px;
    background-color: #f9f9f9;
    border-radius: 8px;
}

/* 操作栏 */
.actions-bar {
    margin-bottom: 20px;
    display: flex;
    gap: 10px;
}

/* 自定义按钮样式 */
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

.btn-danger {
    background-color: #dc3545;
    color: white;
    border-color: #dc3545;
}

.btn-danger:hover {
    background-color: #c82333;
    border-color: #c82333;
}

/* 加载动画 */
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

/* 表格容器 */
.table-container {
    background-color: #fff;
    border-radius: 8px;
    box-shadow: 0 2px 8px rgba(0,0,0,0.05);
    overflow: hidden; /* 保证圆角生效 */
}

/* 自定义表格样式 */
.custom-table {
    width: 100%;
    border-collapse: collapse; /* 合并边框 */
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

/* 状态标签 */
.status-badge {
    padding: 4px 8px;
    border-radius: 12px;
    font-size: 12px;
    color: #fff;
}
.status-active { background-color: #28a745; }
.status-inactive { background-color: #6c757d; }
.status-deleted { background-color: #dc3545; }


/* 分页容器 */
.pagination-container {
    display: flex;
    justify-content: flex-end;
    align-items: center;
    padding: 15px;
    gap: 15px;
}

/* --- 生成用户的弹窗样式 --- */
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
    padding: 20px;
    border-radius: 8px;
    box-shadow: 0 4px 15px rgba(0,0,0,0.2);
    width: 400px;
}

.modal-content h3 {
    margin-top: 0;
    border-bottom: 1px solid #eee;
    padding-bottom: 10px;
}

.modal-body {
    padding: 20px 0;
}

.modal-body label {
    margin-right: 10px;
}

.modal-input {
    padding: 8px;
    border: 1px solid #ccc;
    border-radius: 4px;
    width: 100px;
}

.modal-footer {
    display: flex;
    justify-content: flex-end;
    gap: 10px;
    border-top: 1px solid #eee;
    padding-top: 15px;
}
</style>

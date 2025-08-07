<template>
    <div class="grab-coupon-container">
        <div class="form-wrapper">
            <h2>发布抢券活动</h2>
            <p class="description">请填写以下信息以模拟一次抢券活动。</p>

            <div class="form-group">
                <label for="couponId">券ID:</label>
                <input type="number" id="couponId" v-model.number="form.couponId" placeholder="请输入优惠券ID">
            </div>

            <div class="form-group">
                <label for="couponQuantity">发布数量:</label>
                <input type="number" id="couponQuantity" v-model.number="form.couponQuantity" placeholder="请输入发布的数量">
            </div>

            <div class="form-group">
                <label for="startTime">发布时间:</label>
                <input type="datetime-local" id="startTime" v-model="form.startTimeStr">
                <small>选择一个未来的时间点作为抢券开始时间。</small>
            </div>

            <button class="btn btn-primary btn-publish" @click="publishActivity" :disabled="isLoading">
                <span v-if="isLoading" class="spinner-small"></span>
                <span v-else>发布活动</span>
            </button>
        </div>
    </div>
</template>

<script setup>
import { ref, reactive } from 'vue';
import axios from 'axios';

// 表单数据
const form = reactive({
    couponId: null,
    couponQuantity: null,
    startTimeStr: '', // 使用字符串来绑定 datetime-local 输入框
});

// 加载状态
const isLoading = ref(false);

// “发布活动”按钮点击事件
const publishActivity = async () => {
    // 1. 输入验证
    if (!form.couponId || !form.couponQuantity || !form.startTimeStr) {
        alert('请填写所有必填项！');
        return;
    }
    if (form.couponId <= 0 || form.couponQuantity <= 0) {
        alert('券ID和发布数量必须是正整数！');
        return;
    }

    // 2. 将日期时间字符串转换为秒级时间戳
    const startTime = Math.floor(new Date(form.startTimeStr).getTime() / 1000);
    if (isNaN(startTime)) {
        alert('请输入有效的发布时间！');
        return;
    }

    isLoading.value = true;

    // 3. 调用后端接口
    try {
        // 后端使用 @RequestParam，所以使用 params 配置项
        const response = await axios.post('/api/grab/async', null, {
            params: {
                couponId: form.couponId,
                couponQuantity: form.couponQuantity,
                startTime: startTime,
            }
        });

        if (response.data && response.data.code === '200') {
            const duration = response.data.data;
            alert(`抢券成功！\n本次抢券总用时为 ${duration.toFixed(4)} 秒。`);
        } else {
            alert(`发布失败: ${response.data.msg || '未知错误'}`);
        }
    } catch (error) {
        console.error("发布抢券活动时出错:", error);
        alert('发布活动请求失败，请检查网络或控制台！');
    } finally {
        isLoading.value = false;
    }
};
</script>

<style scoped>
.grab-coupon-container {
    display: flex;
    justify-content: center;
    align-items: flex-start;
    padding: 40px 20px;
    background-color: #f9f9f9;
    height: 100%;
    box-sizing: border-box;
}

.form-wrapper {
    width: 100%;
    max-width: 500px;
    background-color: #fff;
    padding: 30px 40px;
    border-radius: 8px;
    box-shadow: 0 4px 15px rgba(0,0,0,0.08);
}

.form-wrapper h2 {
    text-align: center;
    margin-top: 0;
    margin-bottom: 10px;
    color: #333;
}

.form-wrapper .description {
    text-align: center;
    color: #888;
    margin-bottom: 30px;
    font-size: 14px;
}

.form-group {
    margin-bottom: 20px;
}

.form-group label {
    display: block;
    margin-bottom: 8px;
    font-weight: 600;
    color: #555;
}

.form-group input {
    width: 100%;
    padding: 12px;
    border: 1px solid #ccc;
    border-radius: 4px;
    font-size: 16px;
    box-sizing: border-box;
    transition: border-color 0.3s;
}

.form-group input:focus {
    outline: none;
    border-color: #007bff;
}

.form-group small {
    display: block;
    margin-top: 5px;
    font-size: 12px;
    color: #999;
}

.btn-publish {
    width: 100%;
    padding: 12px;
    font-size: 16px;
    font-weight: 600;
    margin-top: 10px;
}

.btn {
    border: 1px solid #ccc;
    border-radius: 4px;
    background-color: #fff;
    cursor: pointer;
    transition: all 0.3s ease;
}

.btn-primary {
    background-color: #007bff;
    color: white;
    border-color: #007bff;
}

.btn-primary:hover:not(:disabled) {
    background-color: #0056b3;
    border-color: #0056b3;
}

.btn:disabled {
    cursor: not-allowed;
    opacity: 0.6;
}

.spinner-small {
    display: inline-block;
    width: 18px;
    height: 18px;
    border: 2px solid rgba(255, 255, 255, 0.3);
    border-top-color: #fff;
    border-radius: 50%;
    animation: spin 0.8s linear infinite;
}

@keyframes spin {
    0% { transform: rotate(0deg); }
    100% { transform: rotate(360deg); }
}
</style>

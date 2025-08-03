// Global variables
let currentUser = null;
let authToken = localStorage.getItem('authToken');

// DOM elements
const loginBtn = document.getElementById('loginBtn');
const registerBtn = document.getElementById('registerBtn');
const logoutBtn = document.getElementById('logoutBtn');
const loginForm = document.getElementById('loginForm');
const registerForm = document.getElementById('registerForm');
const authForms = document.getElementById('authForms');
const dashboard = document.getElementById('dashboard');
const loadingSpinner = document.getElementById('loadingSpinner');
const notification = document.getElementById('notification');

// API Base URL
const API_BASE_URL = 'http://localhost:8080/api';

// Initialize app
document.addEventListener('DOMContentLoaded', function() {
    setupEventListeners();
    checkAuthStatus();
});

// Event Listeners
function setupEventListeners() {
    // Auth buttons
    loginBtn.addEventListener('click', () => showLoginForm());
    registerBtn.addEventListener('click', () => showRegisterForm());
    logoutBtn.addEventListener('click', logout);

    // Forms
    document.getElementById('loginFormElement').addEventListener('submit', handleLogin);
    document.getElementById('registerFormElement').addEventListener('submit', handleRegister);

    // Dashboard buttons
    document.getElementById('addTransactionBtn').addEventListener('click', () => showModal('transactionForm'));
    document.getElementById('addCategoryBtn').addEventListener('click', () => showModal('categoryForm'));
    document.getElementById('viewReportsBtn').addEventListener('click', loadFinancialSummary);

    // Form submissions
    document.getElementById('transactionFormElement').addEventListener('submit', handleAddTransaction);
    document.getElementById('categoryFormElement').addEventListener('submit', handleAddCategory);

    // Modal close buttons
    document.querySelectorAll('.close').forEach(btn => {
        btn.addEventListener('click', closeModal);
    });

    // Filters
    document.getElementById('filterType').addEventListener('change', filterTransactions);
    document.getElementById('filterCategory').addEventListener('change', filterTransactions);

    // Notification close
    document.querySelector('.notification-close').addEventListener('click', hideNotification);

    // Close modal on outside click
    window.addEventListener('click', (e) => {
        if (e.target.classList.contains('modal')) {
            closeModal();
        }
    });
}

// Authentication Functions
function checkAuthStatus() {
    if (authToken) {
        showDashboard();
        loadDashboardData();
    } else {
        showAuthForms();
    }
}

function showLoginForm() {
    loginForm.classList.remove('hidden');
    registerForm.classList.add('hidden');
}

function showRegisterForm() {
    registerForm.classList.remove('hidden');
    loginForm.classList.add('hidden');
}

function showAuthForms() {
    authForms.classList.remove('hidden');
    dashboard.classList.add('hidden');
    loginBtn.classList.remove('hidden');
    registerBtn.classList.remove('hidden');
    logoutBtn.classList.add('hidden');
}

function showDashboard() {
    authForms.classList.add('hidden');
    dashboard.classList.remove('hidden');
    loginBtn.classList.add('hidden');
    registerBtn.classList.add('hidden');
    logoutBtn.classList.remove('hidden');
}

async function handleLogin(e) {
    e.preventDefault();
    showLoading();

    const username = document.getElementById('loginUsername').value;
    const password = document.getElementById('loginPassword').value;

    try {
        const response = await fetch(`${API_BASE_URL}/auth/login`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({ username, password })
        });

        if (response.ok) {
            const data = await response.json();
            authToken = data.token;
            currentUser = data;
            localStorage.setItem('authToken', authToken);
            localStorage.setItem('currentUser', JSON.stringify(currentUser));
            
            showDashboard();
            loadDashboardData();
            showNotification('Login successful!', 'success');
        } else {
            const error = await response.json();
            showNotification(error.message || 'Login failed', 'error');
        }
    } catch (error) {
        showNotification('Network error. Please try again.', 'error');
    } finally {
        hideLoading();
    }
}

async function handleRegister(e) {
    e.preventDefault();
    showLoading();

    const formData = {
        username: document.getElementById('registerUsername').value,
        email: document.getElementById('registerEmail').value,
        firstName: document.getElementById('registerFirstName').value,
        lastName: document.getElementById('registerLastName').value,
        password: document.getElementById('registerPassword').value
    };

    try {
        const response = await fetch(`${API_BASE_URL}/auth/register`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(formData)
        });

        if (response.ok) {
            const data = await response.json();
            authToken = data.token;
            currentUser = data;
            localStorage.setItem('authToken', authToken);
            localStorage.setItem('currentUser', JSON.stringify(currentUser));
            
            showDashboard();
            loadDashboardData();
            showNotification('Registration successful!', 'success');
        } else {
            const error = await response.json();
            showNotification(error.message || 'Registration failed', 'error');
        }
    } catch (error) {
        showNotification('Network error. Please try again.', 'error');
    } finally {
        hideLoading();
    }
}

function logout() {
    authToken = null;
    currentUser = null;
    localStorage.removeItem('authToken');
    localStorage.removeItem('currentUser');
    showAuthForms();
    showNotification('Logged out successfully', 'success');
}

// Dashboard Functions
async function loadDashboardData() {
    showLoading();
    try {
        await Promise.all([
            loadTransactions(),
            loadCategories(),
            loadFinancialSummary()
        ]);
        updateUserInfo();
    } catch (error) {
        showNotification('Error loading dashboard data', 'error');
    } finally {
        hideLoading();
    }
}

function updateUserInfo() {
    if (currentUser) {
        document.getElementById('userName').textContent = currentUser.firstName;
    }
}

async function loadTransactions() {
    try {
        const response = await fetch(`${API_BASE_URL}/transactions`, {
            headers: {
                'Authorization': `Bearer ${authToken}`
            }
        });

        if (response.ok) {
            const transactions = await response.json();
            displayTransactions(transactions);
        }
    } catch (error) {
        console.error('Error loading transactions:', error);
    }
}

function displayTransactions(transactions) {
    const container = document.getElementById('transactionsList');
    container.innerHTML = '';

    if (transactions.length === 0) {
        container.innerHTML = '<p>No transactions found.</p>';
        return;
    }

    transactions.forEach(transaction => {
        const item = document.createElement('div');
        item.className = `transaction-item ${transaction.type.toLowerCase()}`;
        
        const amount = parseFloat(transaction.amount).toFixed(2);
        const date = new Date(transaction.transactionDate).toLocaleDateString();
        
        item.innerHTML = `
            <div class="transaction-info">
                <h4>${transaction.description}</h4>
                <p>${transaction.category.name} • ${date}</p>
                ${transaction.notes ? `<p><small>${transaction.notes}</small></p>` : ''}
            </div>
            <div class="transaction-amount ${transaction.type.toLowerCase()}">
                ${transaction.type === 'INCOME' ? '+' : '-'}$${amount}
            </div>
        `;
        
        container.appendChild(item);
    });
}

async function loadCategories() {
    try {
        const response = await fetch(`${API_BASE_URL}/categories`, {
            headers: {
                'Authorization': `Bearer ${authToken}`
            }
        });

        if (response.ok) {
            const categories = await response.json();
            displayCategories(categories);
            updateCategorySelects(categories);
        }
    } catch (error) {
        console.error('Error loading categories:', error);
    }
}

function displayCategories(categories) {
    const container = document.getElementById('categoriesList');
    container.innerHTML = '';

    if (categories.length === 0) {
        container.innerHTML = '<p>No categories found.</p>';
        return;
    }

    categories.forEach(category => {
        const item = document.createElement('div');
        item.className = 'category-item';
        
        item.innerHTML = `
            <h4>${category.name}</h4>
            <p>${category.type}</p>
            ${category.description ? `<p><small>${category.description}</small></p>` : ''}
        `;
        
        container.appendChild(item);
    });
}

function updateCategorySelects(categories) {
    const transactionCategory = document.getElementById('transactionCategory');
    const filterCategory = document.getElementById('filterCategory');
    
    // Clear existing options except the first one
    transactionCategory.innerHTML = '<option value="">Select Category</option>';
    filterCategory.innerHTML = '<option value="">All Categories</option>';
    
    categories.forEach(category => {
        const option1 = document.createElement('option');
        option1.value = category.id;
        option1.textContent = category.name;
        transactionCategory.appendChild(option1);
        
        const option2 = document.createElement('option');
        option2.value = category.id;
        option2.textContent = category.name;
        filterCategory.appendChild(option2);
    });
}

async function loadFinancialSummary() {
    try {
        const startDate = new Date();
        startDate.setMonth(startDate.getMonth() - 1);
        const endDate = new Date();
        
        const response = await fetch(
            `${API_BASE_URL}/transactions/summary?startDate=${startDate.toISOString().split('T')[0]}&endDate=${endDate.toISOString().split('T')[0]}`,
            {
                headers: {
                    'Authorization': `Bearer ${authToken}`
                }
            }
        );

        if (response.ok) {
            const summary = await response.json();
            updateStats(summary);
        }
    } catch (error) {
        console.error('Error loading financial summary:', error);
    }
}

function updateStats(summary) {
    document.getElementById('totalIncome').textContent = `$${summary.totalIncome.toFixed(2)}`;
    document.getElementById('totalExpenses').textContent = `$${summary.totalExpense.toFixed(2)}`;
    document.getElementById('netAmount').textContent = `$${summary.netAmount.toFixed(2)}`;
}

// Form Handlers
async function handleAddTransaction(e) {
    e.preventDefault();
    showLoading();

    const formData = {
        description: document.getElementById('transactionDescription').value,
        amount: parseFloat(document.getElementById('transactionAmount').value),
        type: document.getElementById('transactionType').value,
        category: { id: parseInt(document.getElementById('transactionCategory').value) },
        transactionDate: document.getElementById('transactionDate').value,
        notes: document.getElementById('transactionNotes').value
    };

    try {
        const response = await fetch(`${API_BASE_URL}/transactions`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${authToken}`
            },
            body: JSON.stringify(formData)
        });

        if (response.ok) {
            closeModal();
            loadDashboardData();
            showNotification('Transaction added successfully!', 'success');
            e.target.reset();
        } else {
            const error = await response.json();
            showNotification(error.message || 'Failed to add transaction', 'error');
        }
    } catch (error) {
        showNotification('Network error. Please try again.', 'error');
    } finally {
        hideLoading();
    }
}

async function handleAddCategory(e) {
    e.preventDefault();
    showLoading();

    const formData = {
        name: document.getElementById('categoryName').value,
        type: document.getElementById('categoryType').value,
        description: document.getElementById('categoryDescription').value
    };

    try {
        const response = await fetch(`${API_BASE_URL}/categories`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${authToken}`
            },
            body: JSON.stringify(formData)
        });

        if (response.ok) {
            closeModal();
            loadCategories();
            showNotification('Category added successfully!', 'success');
            e.target.reset();
        } else {
            const error = await response.json();
            showNotification(error.message || 'Failed to add category', 'error');
        }
    } catch (error) {
        showNotification('Network error. Please try again.', 'error');
    } finally {
        hideLoading();
    }
}

// Filter Functions
async function filterTransactions() {
    const type = document.getElementById('filterType').value;
    const category = document.getElementById('filterCategory').value;
    
    let url = `${API_BASE_URL}/transactions`;
    const params = new URLSearchParams();
    
    if (type) {
        params.append('type', type);
    }
    if (category) {
        params.append('categoryId', category);
    }
    
    if (params.toString()) {
        url += '?' + params.toString();
    }

    try {
        const response = await fetch(url, {
            headers: {
                'Authorization': `Bearer ${authToken}`
            }
        });

        if (response.ok) {
            const transactions = await response.json();
            displayTransactions(transactions);
        }
    } catch (error) {
        console.error('Error filtering transactions:', error);
    }
}

// Modal Functions
function showModal(modalId) {
    document.getElementById(modalId).classList.remove('hidden');
    
    // Set default date to today
    if (modalId === 'transactionForm') {
        document.getElementById('transactionDate').value = new Date().toISOString().split('T')[0];
    }
}

function closeModal() {
    document.querySelectorAll('.modal').forEach(modal => {
        modal.classList.add('hidden');
    });
}

// Utility Functions
function showLoading() {
    loadingSpinner.classList.remove('hidden');
}

function hideLoading() {
    loadingSpinner.classList.add('hidden');
}

function showNotification(message, type = 'success') {
    const notificationEl = document.getElementById('notification');
    const messageEl = document.getElementById('notificationMessage');
    
    messageEl.textContent = message;
    notificationEl.className = `notification ${type}`;
    notificationEl.classList.remove('hidden');
    
    setTimeout(() => {
        hideNotification();
    }, 5000);
}

function hideNotification() {
    notification.classList.add('hidden');
}
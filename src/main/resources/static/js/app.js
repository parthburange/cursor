// Global variables
let currentUser = null;
let authToken = localStorage.getItem('authToken');

// API Base URL
const API_BASE_URL = 'http://localhost:8080/api';

// Initialize application
document.addEventListener('DOMContentLoaded', function() {
    if (authToken) {
        showDashboard();
        loadDashboardData();
    } else {
        showLoginForm();
    }
});

// Authentication Functions
function showLoginForm() {
    document.getElementById('authContainer').style.display = 'block';
    document.getElementById('loginForm').style.display = 'block';
    document.getElementById('registerForm').style.display = 'none';
    document.getElementById('dashboard').style.display = 'none';
    document.getElementById('authButtons').style.display = 'flex';
    document.getElementById('userInfo').style.display = 'none';
}

function showRegisterForm() {
    document.getElementById('authContainer').style.display = 'block';
    document.getElementById('loginForm').style.display = 'none';
    document.getElementById('registerForm').style.display = 'block';
    document.getElementById('dashboard').style.display = 'none';
    document.getElementById('authButtons').style.display = 'flex';
    document.getElementById('userInfo').style.display = 'none';
}

function showDashboard() {
    document.getElementById('authContainer').style.display = 'none';
    document.getElementById('dashboard').style.display = 'block';
    document.getElementById('authButtons').style.display = 'none';
    document.getElementById('userInfo').style.display = 'flex';
}

async function login(event) {
    event.preventDefault();
    
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
            currentUser = {
                id: data.userId,
                username: data.username,
                firstName: data.firstName,
                lastName: data.lastName
            };
            
            localStorage.setItem('authToken', authToken);
            document.getElementById('userName').textContent = `${currentUser.firstName} ${currentUser.lastName}`;
            
            showDashboard();
            loadDashboardData();
            showMessage('Login successful!', 'success');
        } else {
            const errorData = await response.json();
            showMessage(errorData.message || 'Login failed', 'error');
        }
    } catch (error) {
        showMessage('Network error. Please try again.', 'error');
    }
}

async function register(event) {
    event.preventDefault();
    
    const formData = {
        username: document.getElementById('regUsername').value,
        email: document.getElementById('regEmail').value,
        password: document.getElementById('regPassword').value,
        firstName: document.getElementById('regFirstName').value,
        lastName: document.getElementById('regLastName').value,
        monthlyBudget: parseFloat(document.getElementById('regMonthlyBudget').value) || null,
        currency: 'USD'
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
            currentUser = {
                id: data.userId,
                username: data.username,
                firstName: data.firstName,
                lastName: data.lastName
            };
            
            localStorage.setItem('authToken', authToken);
            document.getElementById('userName').textContent = `${currentUser.firstName} ${currentUser.lastName}`;
            
            showDashboard();
            loadDashboardData();
            showMessage('Registration successful!', 'success');
        } else {
            const errorData = await response.json();
            showMessage(errorData.message || 'Registration failed', 'error');
        }
    } catch (error) {
        showMessage('Network error. Please try again.', 'error');
    }
}

function logout() {
    authToken = null;
    currentUser = null;
    localStorage.removeItem('authToken');
    showLoginForm();
    showMessage('Logged out successfully', 'success');
}

// Dashboard Functions
async function loadDashboardData() {
    await Promise.all([
        loadTransactions(),
        loadCategories(),
        loadFinancialSummary()
    ]);
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

async function loadCategories() {
    try {
        const response = await fetch(`${API_BASE_URL}/categories`, {
            headers: {
                'Authorization': `Bearer ${authToken}`
            }
        });
        
        if (response.ok) {
            const categories = await response.json();
            populateCategorySelects(categories);
        }
    } catch (error) {
        console.error('Error loading categories:', error);
    }
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
            updateSummaryCards(summary);
        }
    } catch (error) {
        console.error('Error loading financial summary:', error);
    }
}

// Transaction Functions
function showAddTransactionForm() {
    document.getElementById('transactionForm').style.display = 'flex';
    document.getElementById('transactionDate').value = new Date().toISOString().split('T')[0];
}

function hideTransactionForm() {
    document.getElementById('transactionForm').style.display = 'none';
    document.getElementById('transactionForm').querySelector('form').reset();
}

async function addTransaction(event) {
    event.preventDefault();
    
    const formData = {
        description: document.getElementById('transactionDescription').value,
        amount: parseFloat(document.getElementById('transactionAmount').value),
        type: document.getElementById('transactionType').value,
        categoryId: parseInt(document.getElementById('transactionCategory').value),
        transactionDate: document.getElementById('transactionDate').value,
        paymentMethod: document.getElementById('transactionPaymentMethod').value,
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
            hideTransactionForm();
            loadDashboardData();
            showMessage('Transaction added successfully!', 'success');
        } else {
            const errorData = await response.json();
            showMessage(errorData.message || 'Failed to add transaction', 'error');
        }
    } catch (error) {
        showMessage('Network error. Please try again.', 'error');
    }
}

// Category Functions
function showAddCategoryForm() {
    document.getElementById('categoryForm').style.display = 'flex';
}

function hideCategoryForm() {
    document.getElementById('categoryForm').style.display = 'none';
    document.getElementById('categoryForm').querySelector('form').reset();
}

async function addCategory(event) {
    event.preventDefault();
    
    const formData = {
        name: document.getElementById('categoryName').value,
        type: document.getElementById('categoryType').value,
        description: document.getElementById('categoryDescription').value,
        colorHex: document.getElementById('categoryColor').value
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
            hideCategoryForm();
            loadCategories();
            showMessage('Category added successfully!', 'success');
        } else {
            const errorData = await response.json();
            showMessage(errorData.message || 'Failed to add category', 'error');
        }
    } catch (error) {
        showMessage('Network error. Please try again.', 'error');
    }
}

// Budget Functions
function showAddBudgetForm() {
    document.getElementById('budgetForm').style.display = 'flex';
    const today = new Date();
    const nextMonth = new Date(today.getFullYear(), today.getMonth() + 1, today.getDate());
    
    document.getElementById('budgetStartDate').value = today.toISOString().split('T')[0];
    document.getElementById('budgetEndDate').value = nextMonth.toISOString().split('T')[0];
}

function hideBudgetForm() {
    document.getElementById('budgetForm').style.display = 'none';
    document.getElementById('budgetForm').querySelector('form').reset();
}

async function addBudget(event) {
    event.preventDefault();
    
    const formData = {
        budgetAmount: parseFloat(document.getElementById('budgetAmount').value),
        startDate: document.getElementById('budgetStartDate').value,
        endDate: document.getElementById('budgetEndDate').value,
        categoryId: document.getElementById('budgetCategory').value ? parseInt(document.getElementById('budgetCategory').value) : null,
        description: document.getElementById('budgetDescription').value,
        isActive: true
    };
    
    try {
        const response = await fetch(`${API_BASE_URL}/budgets`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${authToken}`
            },
            body: JSON.stringify(formData)
        });
        
        if (response.ok) {
            hideBudgetForm();
            showMessage('Budget added successfully!', 'success');
        } else {
            const errorData = await response.json();
            showMessage(errorData.message || 'Failed to add budget', 'error');
        }
    } catch (error) {
        showMessage('Network error. Please try again.', 'error');
    }
}

// Utility Functions
function displayTransactions(transactions) {
    const container = document.getElementById('transactionsList');
    container.innerHTML = '';
    
    if (transactions.length === 0) {
        container.innerHTML = '<div class="transaction-item"><p>No transactions found</p></div>';
        return;
    }
    
    transactions.slice(0, 10).forEach(transaction => {
        const item = document.createElement('div');
        item.className = 'transaction-item';
        
        const amountClass = transaction.type === 'INCOME' ? 'income' : 'expense';
        const amountPrefix = transaction.type === 'INCOME' ? '+' : '-';
        
        item.innerHTML = `
            <div class="transaction-info">
                <div class="transaction-description">${transaction.description}</div>
                <div class="transaction-category">${transaction.categoryName || 'Uncategorized'}</div>
                <div class="transaction-date">${new Date(transaction.transactionDate).toLocaleDateString()}</div>
            </div>
            <div class="transaction-amount ${amountClass}">
                ${amountPrefix}$${transaction.amount.toFixed(2)}
            </div>
        `;
        
        container.appendChild(item);
    });
}

function populateCategorySelects(categories) {
    const transactionSelect = document.getElementById('transactionCategory');
    const budgetSelect = document.getElementById('budgetCategory');
    
    // Clear existing options except the first one
    transactionSelect.innerHTML = '<option value="">Select Category</option>';
    budgetSelect.innerHTML = '<option value="">No Category</option>';
    
    categories.forEach(category => {
        const transactionOption = document.createElement('option');
        transactionOption.value = category.id;
        transactionOption.textContent = category.name;
        transactionSelect.appendChild(transactionOption);
        
        const budgetOption = document.createElement('option');
        budgetOption.value = category.id;
        budgetOption.textContent = category.name;
        budgetSelect.appendChild(budgetOption);
    });
}

function updateSummaryCards(summary) {
    document.getElementById('totalIncome').textContent = `$${summary.totalIncome.toFixed(2)}`;
    document.getElementById('totalExpenses').textContent = `$${summary.totalExpenses.toFixed(2)}`;
    document.getElementById('netAmount').textContent = `$${summary.netAmount.toFixed(2)}`;
    
    // Color code the net amount
    const netAmountElement = document.getElementById('netAmount');
    if (summary.netAmount >= 0) {
        netAmountElement.className = 'amount text-success';
    } else {
        netAmountElement.className = 'amount text-danger';
    }
}

function showMessage(message, type) {
    // Remove existing messages
    const existingMessages = document.querySelectorAll('.message');
    existingMessages.forEach(msg => msg.remove());
    
    // Create new message
    const messageDiv = document.createElement('div');
    messageDiv.className = `message ${type}`;
    messageDiv.textContent = message;
    
    // Insert at the top of the main content
    const mainContent = document.querySelector('.main-content');
    mainContent.insertBefore(messageDiv, mainContent.firstChild);
    
    // Auto-remove after 5 seconds
    setTimeout(() => {
        if (messageDiv.parentNode) {
            messageDiv.remove();
        }
    }, 5000);
}

// Close modals when clicking outside
window.onclick = function(event) {
    const modals = document.querySelectorAll('.form-modal');
    modals.forEach(modal => {
        if (event.target === modal) {
            modal.style.display = 'none';
        }
    });
}
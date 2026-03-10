export interface Widget {
  id: number;
  title: string;
  description: string;
  category: string;
  amount: number;
  date: Date;

  rows?: number;
  columns?: number;

  backgroundColor?: string;
  color?: string;
  // New Trend Data
  trend?: 'up' | 'down' | 'neutral';
  trendValue?: string;
}

export interface Transaction {
  id: number;
  title: string;
  category: string; // e.g., 'Groceries', 'Salary'
  amount: number;
  date: Date;
  type: 'income' | 'expense';
  icon?: string;
}

export interface StatsDTO {
  balance: number;
  income: number;
  expense: number;
  minIncome: number;
  maxIncome: number;
  minExpense: number;
  maxExpense: number;
  latestIncome?: Income;
  latestExpense?: Expense;
}

export interface GraphDTO {
  expenseList: Expense[];
  incomeList: Income[];
}

export interface Income {
  id: number;
  title: string;
  description: string;
  category: string;
  amount: number;
  date: string; // ISO Date string from backend
}

export interface Expense {
  id: number;
  title: string;
  description: string;
  category: string;
  amount: number;
  date: string; // ISO Date string from backend
}

export interface Debt {
  id: number;
  title: string;
  description: string;
  amount: number;
  dueDate: string;
}

export interface Saving {
  id: number;
  title: string;
  description: string;
  targetAmount: number;
  currentAmount: number;
  targetDate: string;
}

export interface Investment {
  id: number;
  title: string;
  description: string;
  symbol: string;
  amountInvested: number;
  currentValue: number;
  date: string;
}

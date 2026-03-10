package com.ayman.distributed.savvy.services.advisor;

import com.ayman.distributed.savvy.model.entity.*;
import com.ayman.distributed.savvy.dto.BudgetDTO;
import com.ayman.distributed.savvy.services.budget.BudgetService;
import com.ayman.distributed.savvy.services.debt.DebtService;
import com.ayman.distributed.savvy.services.expense.ExpenseService;
import com.ayman.distributed.savvy.services.income.IncomeService;
import com.ayman.distributed.savvy.services.investment.InvestmentService;
import com.ayman.distributed.savvy.services.saving.SavingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class FinancialAdvisorService {

    private final IncomeService incomeService;
    private final ExpenseService expenseService;
    private final BudgetService budgetService;
    private final DebtService debtService;
    private final SavingService savingService;
    private final InvestmentService investmentService;
    private final ChatClient chatClient;

    public FinancialAdvisorService(
            IncomeService incomeService,
            ExpenseService expenseService,
            BudgetService budgetService,
            DebtService debtService,
            SavingService savingService,
            InvestmentService investmentService,
            ChatClient.Builder chatClientBuilder) {
        this.incomeService = incomeService;
        this.expenseService = expenseService;
        this.budgetService = budgetService;
        this.debtService = debtService;
        this.savingService = savingService;
        this.investmentService = investmentService;
        this.chatClient = chatClientBuilder.build();
    }

    public String generateFinancialAdvice() {
        log.info("Generating AI financial advice for current user");
        
        List<Income> incomes = incomeService.getAllIncomes();
        List<Expense> expenses = expenseService.getAllExpenses();
        List<BudgetDTO> budgets = budgetService.getAllBudgetsWithStatus();
        List<Debt> debts = debtService.getAllDebts();
        List<Saving> savings = savingService.getAllSavings();
        List<Investment> investments = investmentService.getAllInvestments();

        // Convert cents to standard currency units for the LLM to understand better
        int totalIncome = incomes.stream().mapToInt(Income::getAmount).sum() / 100;
        int totalExpense = expenses.stream().mapToInt(Expense::getAmount).sum() / 100;
        int totalBudget = budgets.stream().mapToInt(b -> b.getBudgetLimit() != null ? b.getBudgetLimit() : 0).sum() / 100;
        int totalDebt = debts.stream().mapToInt(Debt::getAmount).sum() / 100;
        int totalSavings = savings.stream().mapToInt(Saving::getCurrentAmount).sum() / 100;
        int totalInvestments = investments.stream().mapToInt(Investment::getCurrentValue).sum() / 100;

        String template = """
            You are a highly analytical expert financial advisor. Here is your client's current financial profile (amounts are in standard units like USD):
            
            - Total Monthly Income: ${totalIncome}
            - Total Monthly Expenses: ${totalExpense}
            - Total Budget Limit: ${totalBudget}
            - Total Outstanding Debts: ${totalDebt}
            - Total Current Savings: ${totalSavings}
            - Total Investments Value: ${totalInvestments}
            
            Based on this specific profile data, please answer the following questions clearly and concisely:
            1. Am I close to financial freedom?
            2. What can be done to improve my financial health?
            3. Where can my spending or saving be improved specifically?
            4. Where will I be in 5 years if I continue with this exact pattern?
            
            Use professional, encouraging, yet realistic language. Format your response using markdown with clear headings for each question.
            """;

        PromptTemplate promptTemplate = new PromptTemplate(template);
        promptTemplate.add("totalIncome", totalIncome);
        promptTemplate.add("totalExpense", totalExpense);
        promptTemplate.add("totalBudget", totalBudget);
        promptTemplate.add("totalDebt", totalDebt);
        promptTemplate.add("totalSavings", totalSavings);
        promptTemplate.add("totalInvestments", totalInvestments);

        return chatClient.prompt(promptTemplate.create()).call().content();
    }
}

import { useEffect, useState } from "react";
import type { MonthOverviewDTO } from "../../services/finances/dashboard/dashboard.dto";
import { type CurrencyCode } from "../../services/finances/currencies.type";
import Loading from "../../components/common/Loading";
import ErrorMessage from "../../components/common/Error";
import { dashboardService } from "../../services/finances/dashboard/dashboard.service";
import DonutChart from "../../components/common/DonutChart";
import { MonthSelector } from "../../components/common/MonthSelector";
import CurrencySelect from "../../components/finances/CurrencySelect";
import WalletCarousel from "../../components/finances/WalletCarousel";
import TransactionCard from "../../components/finances/TransactionCard";
import { useMediaQuery } from "../../hooks/useMediaQuery";
import TransferenceCard from "../../components/finances/TransferenceCard";
import NetBalanceLineChart, { type TimeSeriesPoint } from "../../components/finances/NetBalanceLineChart";

export default function Dashboard() {
	const [dashboardData, setDashboardData] = useState<MonthOverviewDTO>();
	const currentYearMonth = new Date().toISOString().slice(0, 7);
	const [yearMonth, setYearMonth] = useState<string>(currentYearMonth);
	const [currency, setCurrency] = useState<CurrencyCode>("EUR");
	const [loading, setLoading] = useState<boolean>(true);
	const [error, setError] = useState<string | null>(null);
	const isXL = useMediaQuery("(min-width: 1280px)");

	const fetchDashboard = async () => {
		try {
			setError(null);
			setLoading(true);
			const data: MonthOverviewDTO = await dashboardService.getMonthOverview(yearMonth, currency);
			setDashboardData(data);
		} catch (err) {
			console.error(err);
			setError("Failed to fetch dashboard data");
		} finally {
			setLoading(false);
		}
	};

	useEffect(() => {
		fetchDashboard();
	}, [yearMonth, currency]);

	if (loading) return <Loading />;
	if (error) {
		return (
			<div className="flex justify-center items-center h-full">
				<ErrorMessage
					title="Failed to load dashboard data"
					message="There was a problem connecting to the server. Please try again."
					onRetry={() => fetchDashboard()}
				/>
			</div>
		)
	};

	const chartData: TimeSeriesPoint[] = [
		...(dashboardData?.previousMonthsNetBalance.map((item) => ({
			label: item.yearMonth,
			income: item.income,
			expenses: item.expenses,
			netBalance: item.netBalance
		})) ?? []),	
	];

	return (
		<>
			<div className="w-full p-6 text-textcolor text-4xl font-extralight rounded-lg flex flex-col gap-3">

				<div className="flex items-center justify-between w-full">
					<div className="md:w-1/3" />

					<div className="flex-1 flex md:justify-center w-1/2 md:w-1/3">
						<MonthSelector value={yearMonth} onChange={setYearMonth} />
					</div>

					<div className="text-sm xl:text-lg w-1/2 md:w-1/3 flex justify-end">
						<div className="min-w-40 sm:min-w-40">
							<CurrencySelect value={currency} onChange={setCurrency} disabled={false} />
						</div>
					</div>
				</div>


				<div className="flex flex-col xl:flex-row gap-3 text-xl xl:h-1/11">
					<div className="flex flex-col md:flex-row w-full xl:w-2/3 gap-3">

						<div className="bg-foreground flex flex-col rounded-lg p-4 justify-center items-center w-full">
							<h3 className="text-2xl font-normal w-full text-center">Income</h3>
							<span className="flex justify-center items-center text-nowrap text-[#34D399]">
								+ {dashboardData?.totalIncome}
							</span>
						</div>

						<div className="bg-foreground flex flex-col rounded-lg p-4 justify-center items-center w-full">
							<h3 className="text-2xl font-normal w-full text-center">Expenses</h3>
							<span className="flex justify-center items-center text-nowrap text-[#F87171]">
								- {dashboardData?.totalExpenses}
							</span>
						</div>

					</div>

					<div className="bg-foreground flex flex-col rounded-lg p-4 justify-center items-center w-full xl:w-1/3">
						<h3 className="text-2xl font-normal w-full text-center">Net Balance</h3>
						<span
							className={`flex justify-center items-center text-nowrap ${Number(dashboardData?.netBalance.replace(/[^\d.-]/g, "").replace(/,/g, "").trim() ?? 0) < 0
								? "text-[#F87171]"
								: "text-[#34D399]"
								}`}
						>
							{Number(dashboardData?.netBalance.replace(/[^\d.-]/g, "").replace(/,/g, "").trim() ?? 0) < 0
								? `- ${dashboardData?.netBalance.slice(1) ?? 0}`
								: `+ ${dashboardData?.netBalance ?? 0}`
							}
						</span>
					</div>
				</div>

				<div className={`flex gap-3 flex-col xl:flex-row ${yearMonth === currentYearMonth ? "xl:h-160" : "xl:h-178"}`}>
					<div className="flex flex-col md:flex-row w-full xl:w-2/3 gap-3">

						<div className="bg-foreground flex flex-col rounded-lg w-full p-4 gap-4">
							<h3 className="text-2xl text-center font-normal">Income Breakdown</h3>

							<div className="w-full max-w-[400px] mx-auto">
								<DonutChart categories={dashboardData?.incomeCategories} />
							</div>
						</div>

						<div className="bg-foreground flex flex-col rounded-lg w-full p-4 gap-4">
							<h3 className="text-2xl text-center font-normal">Expenses Breakdown</h3>

							<div className="w-full max-w-[400px] mx-auto">
								<div>
									<DonutChart categories={dashboardData?.expenseCategories} />
								</div>
							</div>
						</div>
					</div>

					{yearMonth === currentYearMonth ?
						(
							<div className="flex flex-col md:flex-row xl:flex-col w-full xl:w-1/3 gap-3 text-2xl">
								<div className="bg-foreground w-full md:w-1/2 xl:w-full xl:h-full p-4 rounded-lg flex items-center justify-center">
									<NetBalanceLineChart
										data={chartData}
										height={300}
										tooltipFormatter={(value: number) => {
											return new Intl.NumberFormat("en-US", {
												style: "currency",
												currency: currency,
											}).format(value);
										}}
										currency={currency}
									/>
								</div>
								<div className="bg-foreground w-full md:w-1/2 xl:w-full xl:h-full p-4 rounded-lg flex flex-col gap-4 items-center justify-center">
									<WalletCarousel wallets={dashboardData?.wallets} />
								</div>
							</div>
						) : (
							<div className="flex flex-col md:flex-row xl:flex-col w-full xl:w-1/3 gap-3 text-2xl">
								<div className="bg-foreground w-full h-100 xl:h-full p-4 rounded-lg">
									<h3 className="text-2xl text-center font-normal mb-5">Last Transactions</h3>
									{dashboardData?.recentTransactions.length ?? 0 > 0 ? (
										<div className="flex flex-col gap-3">
											{dashboardData?.recentTransactions.slice(0, isXL ? 4 : 5).map((item) => {
												return (
													<TransactionCard
														key={item.id}
														transaction={item}
													/>
												)
											})}
										</div>
									) : (
										<div className="flex justify-center items-center bg-background/80 h-78 xl:h-65 rounded-xl border border-primary">
											No data available.
										</div>
									)}
								</div>
								<div className="bg-foreground w-full h-100 xl:h-full p-4 rounded-lg">
									<h3 className="text-2xl text-center font-normal mb-5">Last Transferences</h3>
									{dashboardData?.recentTransferences.length ?? 0 > 0 ? (
										<div className="flex flex-col gap-3">
											{dashboardData?.recentTransferences.map((item) => (
												<TransferenceCard key={item.id} transference={item} />
											))}
										</div>
									) : (
										<div className="flex justify-center items-center bg-background/80 h-78 xl:h-65 rounded-xl border border-primary">
											No data available.
										</div>
									)}
								</div>
							</div>
						)
					}
				</div>

				{yearMonth === currentYearMonth &&
					<div className="flex flex-col xl:flex-row w-full gap-3 text-2xl">
						<div className="flex flex-col md:flex-row gap-3 xl:w-2/3">
							<div className="bg-foreground p-4 rounded-lg w-full md:w-1/2">
								<h3 className="text-2xl text-center font-normal mb-5">Recent Transactions</h3>
								{dashboardData?.recentTransactions.length ?? 0 > 0 ? (
									<div className="flex flex-col gap-3">
										{dashboardData?.recentTransactions.map((item) => (
											<TransactionCard key={item.id} transaction={item} />
										))}
									</div>
								) : (
									<div className="flex justify-center items-center bg-background/80 h-77 border border-primary rounded-xl">
										No data available.
									</div>
								)}
							</div>

							<div className="bg-foreground p-4 rounded-lg w-full md:w-1/2">
								<h3 className="text-2xl text-center font-normal mb-5">Recent Transferences</h3>
								{dashboardData?.recentTransferences.length ?? 0 > 0 ? (
									<div className="flex flex-col gap-3">
										{dashboardData?.recentTransferences.map((item) => (
											<TransferenceCard key={item.id} transference={item} />
										))}
									</div>
								) : (
									<div className="flex justify-center items-center bg-background/80 h-77 border border-primary rounded-xl">
										No data available.
									</div>
								)}
							</div>
						</div>

						<div className="bg-foreground p-4 rounded-lg xl:w-1/3">
							<h3 className="text-2xl text-center font-normal mb-5">Upcoming Bills</h3>
							{dashboardData?.automaticTransactions.length ?? 0 > 0 ? (
								<div className="flex flex-col gap-3">
									{dashboardData?.automaticTransactions.map((item) => (
										<div></div>
									))}
								</div>
							) : (
								<div className="flex justify-center items-center bg-background/80 h-20 xl:h-77 border border-primary rounded-xl">
									No data available.
								</div>
							)}
						</div>
					</div>

				}
			</div>
		</>
	);
}

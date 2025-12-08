import { useEffect, useState } from "react";
import type { DashboardOverviewDTO } from "../../services/finances/dashboard/dashboard.dto";
import type { CurrencyCode } from "../../services/finances/currencies.type";
import Loading from "../../components/common/Loading";
import ErrorMessage from "../../components/common/Error";
import { dashboardService } from "../../services/finances/dashboard/dashboard.service";
import DonutChart from "../../components/common/DonutChart";

export default function Dashboard() {
	const [dashboardData, setDashboardData] = useState<DashboardOverviewDTO>();
	const [yearMonth, setYearMonth] = useState<string>("2025-12");
	const [currency, setCurrency] = useState<CurrencyCode>("EUR");
	const [loading, setLoading] = useState<boolean>(true);
	const [error, setError] = useState<string | null>(null);

	const fetchDashboard = async () => {
		try {
			setError(null);
			setLoading(true);
			const data: DashboardOverviewDTO = await dashboardService.getMonthOverview(yearMonth);
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
	}, []);

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

	return (
		<>
			<div className="w-full p-6 text-textcolor text-4xl font-extralight rounded-lg flex flex-col gap-3">

				<div>
					SELECIONAR CURRENCY + DATA (AO ALTERAR ALGUM -{">"} REFRESH)
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
								? "- "
								: "+ "}
							{dashboardData?.netBalance ?? 0}
						</span>
					</div>
				</div>

				<div className="flex gap-3 flex-col xl:flex-row h-4/5">
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

					<div className="flex flex-col md:flex-row xl:flex-col w-full xl:w-1/3 gap-3">
						<div className="bg-foreground w-full h-40 xl:h-full p-4 rounded-lg">
							Last Transactions
						</div>
						<div className="bg-foreground w-full h-40 xl:h-full p-4 rounded-lg">
							Next Automatic Transactions
						</div>
					</div>
				</div>

				<div className="bg-foreground w-full h-28 p-4 rounded-lg">
					Wallets
				</div>
			</div>
		</>
	);
}

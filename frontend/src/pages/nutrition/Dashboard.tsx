import { useEffect, useState } from 'react';
import Loading from '../../components/common/Loading';
import ErrorMessage from '../../components/common/Error';
import type { WeekOverviewDTO } from '../../services/nutrition/dashboard/dashboard.dto';
import { dashboardService } from '../../services/nutrition/dashboard/dashboard.service';
import { MdLocalFireDepartment, MdEco } from 'react-icons/md'
import { GiSteak, GiWheat, GiAvocado } from 'react-icons/gi'
import { WeekSelector } from '../../components/common/WeekSelector';
import { WeekView } from '../../components/nutrition/WeekView';

export default function NutritionDashboard() {
	const [weekOverview, setWeekOverview] = useState<WeekOverviewDTO>();
	const currentDate = new Date().toISOString().split('T')[0];
	const [date, setDate] = useState<string>(currentDate);
	const [loading, setLoading] = useState<boolean>(false);
	const [error, setError] = useState<string | null>(null);


	const fetchDashboard = async () => {
		try {
			setError(null);
			setLoading(true);
			const data: WeekOverviewDTO = await dashboardService.getWeekOverview(date);
			setWeekOverview(data);
		} catch (err) {
			console.error(err);
			setError("Failed to fetch dashboard data");
		} finally {
			setLoading(false);
		}
	};

	useEffect(() => {
		fetchDashboard();
	}, [date]);

	if (loading) return <Loading />;
	if (error) {
		return (
			<div className="flex justify-center items-center h-full">
				<ErrorMessage
					title="Failed to load dashboard"
					message="There was a problem connecting to the server. Please try again."
					onRetry={() => fetchDashboard()}
				/>
			</div>
		)
	};

	return (
		<>
			<div className="p-8 flex flex-col gap-4 h-full">

				<WeekSelector value={date} onChange={setDate} />

				<div className='flex flex-col sm:flex-row gap-4 text-textcolor'>
					<div className='bg-foreground p-4 flex flex-col w-full justify-center items-center rounded-xl shadow-md'>
						<h2 className='font-bold text-xl sm:text-3xl flex items-center gap-2'>
							<MdLocalFireDepartment className='text-orange-500 text-xl sm:text-3xl' />
							Calories
						</h2>
						<span className='text-lg'>{weekOverview?.macros.totalCalories.toFixed(2)} <span className='text-textcolor/80'>kcal</span></span>
						<span className='text-sm font-extralight text-textcolor/80'>(avg. {weekOverview?.macros.avgCalories.toFixed(2)} kcal/day)</span>
					</div>
					<div className='bg-foreground p-4 flex flex-col w-full justify-center items-center rounded-xl shadow-md'>
						<h2 className='font-bold text-xl sm:text-3xl flex items-center gap-2'>
							<GiSteak className='text-red-500 text-xl sm:text-3xl' />
							Protein
						</h2>
						<span className='text-lg'>{weekOverview?.macros.totalProteins.toFixed(2)} <span className='text-textcolor/80'>g</span></span>
						<span className='text-sm font-extralight text-textcolor/80'>(avg. {weekOverview?.macros.avgProteins.toFixed(2)} g/day)</span>
					</div>
				</div>

				<div className='flex flex-col sm:flex-row gap-4 text-textcolor'>
					<div className='bg-foreground p-4 flex flex-col w-full sm:w-1/3 justify-center items-center rounded-xl shadow-md'>
						<h2 className='font-bold text-xl sm:text-3xl flex items-center gap-2'>
							<GiWheat className='text-yellow-500 text-xl sm:text-3xl' />
							Carbo
						</h2>
						<span className='text-lg'>{weekOverview?.macros.totalCarbo.toFixed(2)} <span className='text-textcolor/80'>g</span></span>
						<span className='text-sm font-extralight text-textcolor/80'>(avg. {weekOverview?.macros.avgCarbo.toFixed(2)} g/day)</span>
					</div>
					<div className='flex flex-row gap-4 w-full sm:w-2/3'>
						<div className='bg-foreground p-4 flex flex-col w-full justify-center items-center rounded-xl shadow-md'>
							<h2 className='font-bold text-xl sm:text-3xl flex items-center gap-2'>
								<GiAvocado className='text-blue-500 text-xl sm:text-3xl' />
								Fat
							</h2>
							<span className='text-lg'>{weekOverview?.macros.totalFat.toFixed(2)} <span className='text-textcolor/80'>g</span></span>
							<span className='text-sm font-extralight text-textcolor/80'>(avg. {weekOverview?.macros.avgFat.toFixed(2)} g/day)</span>
						</div>
						<div className='bg-foreground p-4 flex flex-col w-full justify-center items-center rounded-xl shadow-md'>
							<h2 className='font-bold text-xl sm:text-3xl flex items-center gap-2'>
								<MdEco className='text-green-500 text-xl sm:text-3xl' />
								Fiber
							</h2>
							<span className='text-lg'>{weekOverview?.macros.totalFiber.toFixed(2)} <span className='text-textcolor/80'>g</span></span>
							<span className='text-sm font-extralight text-textcolor/80'>(avg. {weekOverview?.macros.avgFiber.toFixed(2)} g/day)</span>
						</div>
					</div>
				</div>

				<WeekView weekOverview={weekOverview?.week || []} currentDate={currentDate} />
			</div >
		</>
	);
}
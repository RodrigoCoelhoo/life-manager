import { useEffect, useState } from 'react';
import Loading from '../../components/common/Loading';
import ErrorMessage from '../../components/common/Error';
import ConsistencyMap from '../../components/training/ConsistencyMap';
import { MonthSelector } from '../../components/common/MonthSelector';
import type { MonthOverviewDTO } from '../../services/training/dashboard/dashboard.dto';
import { dashboardService } from '../../services/training/dashboard/dashboard.service';
import { FaDumbbell, FaRunning } from 'react-icons/fa';
import { RiHeartPulseFill } from 'react-icons/ri';
import React from 'react';
import { ExercisesSearch } from '../../components/training/ExercisesSearch';
import { exerciseService } from '../../services/training/exercise/exercise.service'
import { type ExerciseStats } from '../../services/training/exercise/exercise.dto';
import type { E1RMPoint } from '../../components/training/AllTimeE1RMLineChart';
import AllTimeE1RMLineChart from '../../components/training/AllTimeE1RMLineChart';

export default function TrainingDashboard() {
	const [monthOverview, setMonthOverview] = useState<MonthOverviewDTO>();
	const currentYearMonth = new Date().toISOString().slice(0, 7);
	const [date, setDate] = useState<string>(currentYearMonth);
	const [loading, setLoading] = useState<boolean>(false);
	const [error, setError] = useState<string | null>(null);

	const [selectedExerciseId, setSelectedExerciseId] = useState<number>(-1);
	const [activeExercise, setActiveExercise] = useState<ExerciseStats>();
	const [activeE1RMData, setActiveE1RMData] = useState<E1RMPoint[]>();

	const fetchDashboard = async () => {
		try {
			setError(null);
			setLoading(true);
			const data: MonthOverviewDTO = await dashboardService.getMonthOverview(date);
			setMonthOverview(data);
		} catch (err) {
			console.error(err);
			setError("Failed to fetch dashboard data");
		} finally {
			setLoading(false);
		}
	};

	const fetchExercise = async () => {
		try {
			if (selectedExerciseId === -1) return;
			const data: ExerciseStats = await exerciseService.getExerciseStats(selectedExerciseId);
			setActiveExercise(data);
			const e1rmData: E1RMPoint[] = Object.entries(data.monthlyMaxE1RM)
				.map(([month, e1rm]) => ({ label: month, e1rm }));
			setActiveE1RMData(e1rmData);
		} catch (err) {
			console.error(err);
			setError("Failed to fetch exercise data");
		}
	}

	useEffect(() => {
		fetchDashboard();
	}, [date]);

	useEffect(() => {
		fetchExercise();
	}, [selectedExerciseId]);

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

	const secondsToTime = (secs: number) => {
		const days = Math.floor(secs / 86400);
		const hours = Math.floor((secs % 86400) / 3600);
		const minutes = Math.floor((secs % 3600) / 60);
		const seconds = secs % 60;

		const time = [hours, minutes, seconds]
			.map(v => v.toString().padStart(2, '0'))
			.join(':');

		return days > 0 ? `${days}d ${time}` : time;
	};

	const metersToKm = (meters: number) => {
		const km = meters / 1000;
		return `${km.toFixed(2)}km`;
	}

	return (
		<div className="space-y-4 flex flex-col text-textcolor py-4 px-8 h-full">
			<div className='flex flex-col gap-4 xl:h-1/2'>
				<MonthSelector value={date} onChange={setDate} />

				<div className='flex flex-col lg:flex-row w-full gap-4'>
					<div className='flex flex-row gap-4 flex-3'>
						<div className='flex-1 bg-foreground shadow-md p-6 xl:p-2 2xl:p-6 rounded-xl text-lg lg:text-lg xl:text-lg 2xl:text-xl font-semibold flex items-center justify-center'>
							{monthOverview?.dates.length} {monthOverview?.dates.length === 1 ? "session" : "sessions"}
						</div>
						<div className='flex-2 bg-foreground shadow-md p-4 rounded-xl text-lg font-semibold flex items-center justify-center'>
							<div className='flex flex-row gap-4 items-center justify-center'>
								<div className='bg-primary/60 p-1 rounded-lg w-fit h-fit text-2xl'>
									<FaDumbbell />
								</div>
								<div className='flex flex-col'>
									<span>
										Volume
									</span>
									<span className='font-light'>
										{monthOverview?.volume.toFixed(2)} kg
									</span>
								</div>
							</div>
						</div>
					</div>
					<div className='flex flex-row gap-4 flex-4'>
						<div className='flex-2 bg-foreground shadow-md p-6 rounded-xl text-lg font-semibold flex items-center justify-center'>
							<div className='flex flex-row gap-4 items-center justify-center'>
								<div className='bg-secondary/60 p-1 rounded-lg w-fit h-fit text-2xl'>
									<RiHeartPulseFill />
								</div>
								<div className='flex flex-col'>
									<span>
										Cardio
									</span>
									<span className='font-light'>
										{secondsToTime(monthOverview?.timeSecs || 0)}
									</span>
								</div>
							</div>
						</div>
						<div className='flex-2 bg-foreground shadow-md p-6 rounded-xl text-lg font-semibold flex items-center justify-center'>
							<div className='flex flex-row gap-4 items-center justify-center'>
								<div className='bg-secondary/60 p-1 rounded-lg w-fit h-fit text-2xl'>
									<FaRunning />
								</div>
								<div className='flex flex-col'>
									<span>
										Distance
									</span>
									<span className='font-light'>
										{metersToKm(monthOverview?.distance || 0)}
									</span>
								</div>
							</div>
						</div>
					</div>
				</div>

				<div className='flex flex-col sm:flex-row gap-4'>
					<div className='bg-foreground shadow-md p-6 rounded-xl w-full sm:w-fit flex justify-center items-center'>
						<ConsistencyMap
							dates={monthOverview?.dates}
							month={Number(date.split("-")[1]) - 1}
							year={Number(date.split("-")[0])}
							squareSize={16}
							gap={5}
							showDayLabels
						/>
					</div>

					<div className='bg-foreground w-full shadow-md p-6 rounded-xl'>
						<div className='grid grid-cols-[4fr_2fr_2fr_2fr] bg-background shadow-md px-2 rounded-xl'>
							<div className='font-semibold text-sm md:text-base border-b border-primary/60 '>Exercise Name</div>
							<div className='font-semibold text-sm md:text-base border-b border-primary/60 '>Max Weight</div>
							<div className='font-semibold text-sm md:text-base border-b border-primary/60 '>Best Set</div>
							<div className='font-semibold text-sm md:text-base border-b border-primary/60 '>E1MR</div>
						</div>
						{monthOverview?.exercisePRS.length || 0 > 0 ? (<div className='h-60 sm:h-41 grid grid-cols-[4fr_2fr_2fr_2fr] bg-background shadow-md px-2 rounded-xl overflow-y-auto'>

							{monthOverview?.exercisePRS.map((item, index) => {
								const rowStyle = index % 2 === 0 ? "bg-foreground/90" : "bg-background/70";
								const itemStyle = "font-extralight text-sm p-1";
								return (
									<React.Fragment>
										<div className={`${rowStyle} ${itemStyle}`}>
											{item.exerciseName}
										</div>
										<div className={`${rowStyle} ${itemStyle}`}>
											{item.maxWeight.toFixed(2)} kg
										</div>
										<div className={`${rowStyle} ${itemStyle}`}>
											{item.bestVolumeSet.reps} x {item.bestVolumeSet.weight} kg
										</div>
										<div className={`${rowStyle} ${itemStyle}`}>
											{item.bestE1RM.toFixed(2)} kg
										</div>
									</React.Fragment>
								);
							})
							}
						</div>) : (
							<div className='bg-background shadow-md px-2 rounded-xl h-41 text-xl flex items-center justify-center'>
								No data available.
							</div>
						)}
					</div>
				</div>
			</div>

			<div className='w-full xl:h-1/2 flex flex-col lg:flex-row gap-4 mt-4'>
				<div className='w-full flex flex-col gap-4'>
					<div>
						<h1 className='font-bold text-xl'>Exercise | All Time Stats</h1>
						<h2 className='font-extralight text-base'>{activeExercise?.name}</h2>
					</div>

					<ExercisesSearch getExercises={exerciseService.getExercises} setExercise={setSelectedExerciseId} />


					<div className='flex flex-col gap-4 h-full'>
						<div className='flex flex-col sm:flex-row gap-4 h-full'>
							<div className='bg-foreground shadow-md p-4 rounded-xl w-full flex flex-col'>
								<span className='font-semibold'>Volume</span>
								<span className='font-extralight'>{(activeExercise?.volume || 0).toFixed(2)} kg</span>
							</div>
							<div className='bg-foreground shadow-md p-4 rounded-xl w-full flex flex-col'>
								<span className='font-semibold'>Sets</span>
								<span className='font-extralight'>{(activeExercise?.sets || 0)}</span>
							</div>
							<div className='bg-foreground shadow-md p-4 rounded-xl w-full flex flex-col'>
								<span className='font-semibold'>  Reps</span>
								<span className='font-extralight'>{(activeExercise?.reps || 0)}</span>
							</div>
						</div>
						<div className='flex flex-col sm:flex-row gap-4 h-full'>
							<div className='bg-foreground shadow-md p-4 rounded-xl w-full flex flex-col'>
								<span className='font-semibold'>Max Weight</span>
								<span className='font-extralight'>{(activeExercise?.maxWeight || 0).toFixed(2)} kg</span>
							</div>
							<div className='bg-foreground shadow-md p-4 rounded-xl w-full flex flex-col'>

								<span className='font-semibold'>Best Rep Set</span>
								<span className='font-extralight'>{(activeExercise?.bestRepSet.reps || 0)} x {(activeExercise?.bestRepSet.weight || 0).toFixed(2)} kg</span>
							</div>
							<div className='bg-foreground shadow-md p-4 rounded-xl w-full flex flex-col'>
								<span className='font-semibold'>E1RM</span>
								<span className='font-extralight'>{(activeExercise?.e1rm || 0).toFixed(2)} kg</span>
							</div>
						</div>
					</div>
				</div>
				<div className='w-full bg-foreground shadow-md rounded-xl'>
					<h2 className='font-semibold text-xl p-5'>E1RM Progress (Last 6 Months)</h2>
					<div className='px-2'>
						<AllTimeE1RMLineChart data={activeE1RMData} height={350} />
					</div>
				</div>
			</div>
		</div>
	);
}
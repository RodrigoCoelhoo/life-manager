import { useEffect, useState } from 'react';
import Loading from '../../components/common/Loading';
import ErrorMessage from '../../components/common/Error';
import type { PageResponseDTO } from '../../services/api.dto';
import { Pagination } from '../../components/common/Pagination';
import { Modal } from '../../components/common/Modal';
import toast from 'react-hot-toast';
import { mealService } from '../../services/nutrition/meal/meal.service';
import type { MealDetailsDTO, MealDTO } from '../../services/nutrition/meal/meal.dto';
import MealCard from '../../components/nutrition/MealCard';
import { MealForm } from '../../components/nutrition/MealForm';
import { GiForkKnifeSpoon } from 'react-icons/gi';

export default function Meals() {
	const [meals, setMeals] = useState<MealDetailsDTO[]>([]);
	const [loading, setLoading] = useState<boolean>(false);
	const [error, setError] = useState<string | null>(null);

	const [page, setPage] = useState<number>(1);
	const [totalPages, setTotalPages] = useState<number>(1);
	const [totalElements, setTotalElements] = useState<number>(1);
	const [elementsPerPage, setElementsPerPage] = useState<number>(18);

	const [createMealOpen, setCreateMealOpen] = useState<boolean>(false);

	const fetchMeals = async () => {
		try {
			setError(null);
			setLoading(true);
			const data: PageResponseDTO<MealDetailsDTO> = await mealService.getMeals(page - 1, elementsPerPage);

			setMeals(data.content);
			setTotalPages(data.totalPages);
			setTotalElements(Number(data.totalElements));
		} catch (err) {
			console.error(err);
			setError("Failed to fetch meals");
		} finally {
			setLoading(false);
		}
	};

	const createMeal = async (meal: MealDTO) => {
		try {
			setLoading(true);
			const data: MealDetailsDTO = await mealService.createMeal(meal);
			setMeals(prev => {
				const updated = [data, ...prev];

				if (updated.length > elementsPerPage) {
					updated.pop();
				}

				return updated;
			});
			setTotalElements(prev => prev + 1);
		} catch (err) {
			console.error(err);
			toast.error("Failed to create meal");
		} finally {
			setLoading(false);
		}
	};

	const updateMeal = async (id: number, meal: MealDTO) => {
		try {
			setLoading(true);
			const updated: MealDetailsDTO = await mealService.updateMeal(id, meal);
			setMeals(prev => prev.map(e => (e.meal.id === id ? updated : e)));
		} catch (err) {
			console.error(err);
			toast.error("Failed to update meal");
		} finally {
			setLoading(false);
		}
	};

	const deleteMeal = async (id: number) => {
		try {
			setLoading(true);
			await mealService.deleteMeal(id);
			fetchMeals();
			setTotalElements(prev => prev - 1);
		} catch (err) {
			toast.error("Failed to delete meal");
		} finally {
			setLoading(false);
		}
	};

	useEffect(() => {
		fetchMeals();
	}, [page, elementsPerPage]);

	if (loading) return <Loading />;
	if (error) {
		return (
			<div className="flex justify-center items-center h-full">
				<ErrorMessage
					title="Failed to load meals"
					message="There was a problem connecting to the server. Please try again."
					onRetry={() => fetchMeals()}
				/>
			</div>
		)
	};

	return (
		<>
			<div className="w-full p-6 text-textcolor flex flex-col gap-4">
				<div className="flex items-center justify-between gap-4">
					<button
						className="bg-primary p-2 px-4 rounded-xl cursor-pointer hover:bg-primary/80 font-semibold"
						onClick={() => setCreateMealOpen(true)}
					>
						Create +
					</button>

					<div className="flex items-center gap-3">
						<div className="flex gap-3 items-center">
							<label htmlFor="exercisesPerPage" className="text-sm mb-1 font-extralight">
								Meals per page
							</label>

							<select
								id="exercisesPerPage"
								name="exercisesPerPage"
								required
								className="form-input w-14"
								value={elementsPerPage}
								onChange={(e) => setElementsPerPage(Number(e.target.value))}
							>
								<option value="12">12</option>
								<option value="18">18</option>
								<option value="24">24</option>
							</select>
						</div>

						<span className="text-secondary"> {totalElements} {totalElements === 1 ? "meal" : "meals"} </span>
					</div>

				</div>

				<div className="flex flex-col gap-2">
					{meals.length === 0 ? (
						<div className="flex items-center justify-center min-h-[70vh]">
							<div className="flex flex-col items-center justify-center rounded-2xl p-8 text-center">
								<div className="mb-4 text-primary/70">
									<GiForkKnifeSpoon size={32} />
								</div>

								<p className="text-xl font-medium text-textcolor mb-2">
									No meals yet
								</p>

								<p className="text-sm text-textcolor/60 max-w-md">
									Create your first meal to start tracking your macros
								</p>
							</div>
						</div>
					) : (
						<div className="grid grid-cols-1 md:grid-cols-2 2xl:grid-cols-3 gap-6">
							{meals.map((meal) => (
								<MealCard
									key={meal.meal.id}
									{...meal}
									onUpdate={updateMeal}
									onDelete={deleteMeal}
								/>
							))}
						</div>
					)}
				</div>

				<div className="mb-4">
					<Pagination
						currentPage={page}
						totalPages={totalPages}
						onPageChange={(p: number) => setPage(p)}
					/>
				</div>
			</div>

			<Modal isOpen={createMealOpen} onClose={() => setCreateMealOpen(false)}>
				<MealForm
					onClose={() => setCreateMealOpen(false)}
					onCreate={createMeal}
				/>
			</Modal>
		</>
	);
}
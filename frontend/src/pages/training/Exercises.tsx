import { useEffect, useState } from "react";
import type { ExerciseDTO, ExerciseResponseDTO, ExerciseUpdateDTO } from "../../services/training/exercise/exercise.dto";
import type { PageResponseDTO } from "../../services/api.dto";
import { exerciseService } from "../../services/training/exercise/exercise.service";
import ExerciseCard from "../../components/training/ExerciseCard";
import Loading from "../../components/common/Loading";
import ErrorMessage from "../../components/common/Error";
import { Modal } from "../../components/common/Modal";
import ExerciseForm from "../../components/training/ExerciseForm";
import { Pagination } from "../../components/common/Pagination";

export default function Exercises() {
	const [exercises, setExercises] = useState<ExerciseResponseDTO[]>([]);
	const [loading, setLoading] = useState<boolean>(true);
	const [error, setError] = useState<string | null>(null);

	const [page, setPage] = useState<number>(1);
	const [totalPages, setTotalPages] = useState<number>(1);
	const [totalElements, setTotalElements] = useState<number>(1);
	const [elementsPerPage, setElementsPerPage] = useState<number>(18);

	const [createExerciseOpen, setCreateExerciseOpen] = useState<boolean>(false);

	const fetchExercises = async () => {
		try {
			setError(null);
			setLoading(true);
			const data: PageResponseDTO<ExerciseResponseDTO> = await exerciseService.getExercises(page - 1, elementsPerPage);

			setExercises(data.content);
			setTotalPages(data.totalPages);
			setTotalElements(Number(data.totalElements));
		} catch (err) {
			console.error(err);
			setError("Failed to fetch exercises");
		} finally {
			setLoading(false);
		}
	};

	const createExercise = async (exercise: ExerciseDTO) => {
		try {
			setLoading(true);
			const data: ExerciseResponseDTO = await exerciseService.createExercise(exercise);
			setExercises(prev => {
				const updated = [data, ...prev];

				if (updated.length > elementsPerPage) {
					updated.pop();
				}

				return updated;
			});

			setTotalElements(prev => prev + 1);
		} catch (err) {
			console.error(err);
			setError("Failed to create exercise");
		} finally {
			setLoading(false);
		}
	};

	const updateExercise = async (id: number, updatedData: ExerciseUpdateDTO) => {
		try {
			setLoading(true);
			const updated = await exerciseService.updateExercise(id, updatedData);
			setExercises(prev => prev.map(e => (e.id === id ? updated : e)));
		} catch (err) {
			setError("Failed to update exercise");
		} finally {
			setLoading(false);
		}
	};

	const deleteExercise = async (id: number) => {
		try {
			setLoading(true);
			await exerciseService.deleteExercise(id);
			fetchExercises();
			setTotalElements(prev => prev - 1);
		} catch (err) {
			setError("Failed to delete exercise");
		} finally {
			setLoading(false);
		}
	};


	useEffect(() => {
		fetchExercises();
	}, [page, elementsPerPage]);

	if (loading) return <Loading />;
	if (error) {
		return (
			<div className="flex justify-center items-center h-full">
				<ErrorMessage
					title="Failed to load exercises"
					message="There was a problem connecting to the server. Please try again."
					onRetry={() => fetchExercises()}
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
						onClick={() => setCreateExerciseOpen(true)}
					>
						Create +
					</button>

					<div className="flex items-center gap-3">
						<div className="flex gap-3 items-center">
							<label htmlFor="exercisesPerPage" className="text-sm mb-1 font-extralight">
								Exercises per page
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

						<span className="text-secondary"> {totalElements} {totalElements === 1 ? "exercise" : "exercises"} </span>

					</div>
				</div>

				<div className="flex flex-col gap-2">
					<div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
						{exercises.map((exercise) => (
							<ExerciseCard
								key={exercise.id}
								{...exercise}
								onUpdate={updateExercise}
								onDelete={deleteExercise}
							/>
						))}
					</div>
				</div>

				<div className="mb-4">
					<Pagination
						currentPage={page}
						totalPages={totalPages}
						onPageChange={(p: number) => setPage(p)}
					/>
				</div>
			</div>

			<Modal isOpen={createExerciseOpen} onClose={() => setCreateExerciseOpen(false)}>
				<ExerciseForm
					onClose={() => setCreateExerciseOpen(false)}
					onCreate={createExercise}
				/>
			</Modal>
		</>
	);
}

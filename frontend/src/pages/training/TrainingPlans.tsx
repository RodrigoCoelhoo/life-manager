import { useEffect, useState } from "react";
import type { PageResponseDTO } from "../../services/api.dto";
import { trainingPlanService } from "../../services/training/training-plan/training-plan.service";
import TrainingPlanCard from "../../components/training/TrainingPlanCard";
import Loading from "../../components/common/Loading";
import ErrorMessage from "../../components/common/Error";
import { Modal } from "../../components/common/Modal";
import { Pagination } from "../../components/common/Pagination";
import type { TrainingPlanDTO, TrainingPlanResponseDTO, TrainingPlanUpdateDTO } from "../../services/training/training-plan/training-plan.dto";
import TrainingPlanForm from "../../components/training/TrainingPlanForm";

export default function TrainingPlans() {
	const [trainingPlans, setTrainingPlans] = useState<TrainingPlanResponseDTO[]>([]);
	const [loading, setLoading] = useState<boolean>(true);
	const [error, setError] = useState<string | null>(null);

	const [page, setPage] = useState<number>(1);
	const [totalPages, setTotalPages] = useState<number>(1);
	const [totalElements, setTotalElements] = useState<number>(1);
	const [elementsPerPage, setElementsPerPage] = useState<number>(18);

	const [createTrainingPlanOpen, setCreateTrainingPlanOpen] = useState<boolean>(false);

	const fetchTrainingPlans = async () => {
		try {
			setError(null);
			setLoading(true);
			const data: PageResponseDTO<TrainingPlanResponseDTO> = await trainingPlanService.getTrainingPlans(page - 1, elementsPerPage);

			setTrainingPlans(data.content);
			setTotalPages(data.totalPages);
			setTotalElements(Number(data.totalElements));
		} catch (err) {
			console.error(err);
			setError("Failed to fetch training plans");
		} finally {
			setLoading(false);
		}
	};

	const createTrainingPlan = async (trainingPlan: TrainingPlanDTO) => {
		try {
			setLoading(true);
			const data: TrainingPlanResponseDTO = await trainingPlanService.createTrainingPlan(trainingPlan);
			setTrainingPlans(prev => {
				const updated = [data, ...prev];

				if (updated.length > elementsPerPage) {
					updated.pop();
				}

				return updated;
			});
			setTotalElements(prev => prev + 1);
		} catch (err) {
			console.error(err);
			setError("Failed to create training plan");
		} finally {
			setLoading(false);
		}
	};

	const updateTrainingPlan = async (id: number, updatedData: TrainingPlanUpdateDTO) => {
		try {
			setLoading(true);
			const updated = await trainingPlanService.updateTrainingPlan(id, updatedData);
			setTrainingPlans(prev => prev.map(e => (e.id === id ? updated : e)));
		} catch (err) {
			setError("Failed to update training plan");
		} finally {
			setLoading(false);
		}
	};

	const deleteTrainingPlan = async (id: number) => {
		try {
			setLoading(true);
			await trainingPlanService.deleteTrainingPlan(id);
			fetchTrainingPlans();
			setTotalElements(prev => prev - 1);
		} catch (err) {
			setError("Failed to delete training plan");
		} finally {
			setLoading(false);
		}
	};

	useEffect(() => {
		fetchTrainingPlans();
	}, [page, elementsPerPage]);

	if (loading) return <Loading />;
	if (error) {
		return (
			<div className="flex justify-center items-center h-full">
				<ErrorMessage
					title="Failed to load training plans"
					message="There was a problem connecting to the server. Please try again."
					onRetry={() => fetchTrainingPlans()}
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
						onClick={() => setCreateTrainingPlanOpen(true)}
					>
						Create +
					</button>

					<div className="flex items-center gap-3">
						<div className="flex gap-3 items-center">
							<label htmlFor="exercisesPerPage" className="text-sm mb-1 font-extralight">
								Training plans per page
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

						<span className="text-secondary"> {totalElements} {totalElements === 1 ? "training plan" : "training plans"} </span>

					</div>
				</div>

				<div className="flex flex-col gap-2">
					<div className="grid grid-cols-1 md:grid-cols-2 2xl:grid-cols-3 gap-6">
						{trainingPlans.map((trainingPlan) => (
							<TrainingPlanCard
								key={trainingPlan.id}
								{...trainingPlan}
								onUpdate={updateTrainingPlan}
								onDelete={deleteTrainingPlan}
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

			<Modal isOpen={createTrainingPlanOpen} onClose={() => setCreateTrainingPlanOpen(false)}>
				<TrainingPlanForm
					onClose={() => setCreateTrainingPlanOpen(false)}
					onCreate={createTrainingPlan}
				/>
			</Modal>
		</>
	);
}

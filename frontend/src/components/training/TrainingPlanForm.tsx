import { useState } from "react";
import type { TrainingPlanDTO, TrainingPlanResponseDTO, TrainingPlanUpdateDTO } from "../../services/training/training-plan/training-plan.dto";
import type { ExerciseSimpleDTO } from "../../services/training/exercise/exercise.dto";
import { FaRegTrashAlt } from "react-icons/fa";

interface ExerciseFormProps {
	trainingPlan?: TrainingPlanResponseDTO;
	onClose: () => void;
	onCreate?: (trainingPlan: TrainingPlanDTO) => Promise<void>;
	onUpdate?: (id: number, data: TrainingPlanUpdateDTO) => Promise<void>;
	onDelete?: (id: number) => Promise<void>;
}

export default function ExerciseForm({ onClose, trainingPlan, onCreate, onUpdate, onDelete }: ExerciseFormProps) {
	const [name, setName] = useState(trainingPlan?.name || "");
	const [description, setDescription] = useState(trainingPlan?.description || "");
	const [exercises, setExercises] = useState<ExerciseSimpleDTO[]>(trainingPlan?.exercises || []);

	const handleUpdate = async () => {
		if (!onUpdate || !trainingPlan) return;
		await onUpdate(trainingPlan.id, { name, description, exerciseIds: exercises.map(ex => ex.id) });
		onClose();
	};

	const handleDelete = async () => {
		if (!onDelete || !trainingPlan) return;
		await onDelete(trainingPlan.id);
		onClose();
	};

	const handleCreate = async () => {
		if (!onCreate) return;
		await onCreate({ name, description });
		onClose();
	};

	return (
		<div className="
			bg-foreground 
			rounded-xl 
			shadow-lg 
			p-12
			max-h-[85vh] 
			overflow-y-auto 
			flex flex-col
			gap-4
			text-textcolor"
		>
			<div>
				<button
					onClick={onClose}
					className="absolute top-2 right-4 text-white text-xl hover:text-gray-200 p-2 cursor-pointer"
				>
					✕
				</button>
				<form className="flex flex-col space-y-4 w-80 sm:w-100" onSubmit={trainingPlan ? handleUpdate : handleCreate}>
					<div className="flex flex-col text-left">
						<label htmlFor="name" className="text-sm mb-1">
							Name
						</label>
						<input
							type="text"
							id="name"
							name="name"
							placeholder="Exercise name"
							className="form-input"
							required
							value={name}
							onChange={(event) => setName(event.target.value)}
						/>
					</div>

					<div className="flex flex-col text-left">
						<label htmlFor="description" className="text-sm mb-1">
							Description
						</label>
						<textarea
							id="description"
							name="description"
							placeholder="Exercise description"
							className="form-input h-25"
							value={description}
							onChange={(event) => setDescription(event.target.value)}
						/>
					</div>

					{trainingPlan &&
						<div>
							<label htmlFor="description" className="text-sm mb-1 mt-3">
								Exercises
							</label>

							<div className="overflow-y-auto max-h-75 border form-input p-0">
								{trainingPlan.exercises.map((exercise, index) => (
									<div className={`${index % 2 === 0 ? 'bg-gray-400/5' : 'bg-background/50'} p-2 flex justify-between items-center gap-2`}>
										<span>{index + 1}. {exercise.name}</span>

										<div className="flex justify-center items-center gap-2">
											<div className="text-xs text-gray-400">
												{exercise.type}
											</div>
											<div className="text-red-400/80 hover:rounded-full hover:bg-red-500/20 p-1 cursor-pointer">
												<FaRegTrashAlt />
											</div>
										</div>
									</div>
								))}
							</div>
						</div>
					}

					<button
						type="submit"
						className="form-submit"
					>
						{trainingPlan ? "Save Changes" : "Create"}
					</button>
				</form>
			</div>

			{trainingPlan &&
				<div className="border-t border-secondary/50">
					<button
						className="form-submit bg-red-500 w-full hover:bg-red-600"
						onClick={handleDelete}
					>
						Delete Training Plan
					</button>
				</div>
			}
		</div>
	);
}

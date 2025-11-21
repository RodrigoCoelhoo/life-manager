import { useRef, useState } from "react";
import type { TrainingPlanDTO, TrainingPlanResponseDTO, TrainingPlanUpdateDTO } from "../../services/training/training-plan/training-plan.dto";
import type { ExerciseSimpleDTO } from "../../services/training/exercise/exercise.dto";
import { FaRegTrashAlt, FaArrowDown, FaArrowUp } from "react-icons/fa";
import { Modal } from "../common/Modal";
import { SearchList } from "../common/SearchList";
import { InputField } from "../common/InputField";
import { descriptionRules, nameRules } from "../../rules/rules";
import { exerciseService } from "../../services/training/exercise/exercise.service";

interface TrainingPlanFormProps {
	trainingPlan?: TrainingPlanResponseDTO;
	onClose: () => void;
	onCreate?: (trainingPlan: TrainingPlanDTO) => Promise<void>;
	onUpdate?: (id: number, data: TrainingPlanUpdateDTO) => Promise<void>;
	onDelete?: (id: number) => Promise<void>;
}

export default function TrainingPlanForm({ onClose, trainingPlan, onCreate, onUpdate, onDelete }: TrainingPlanFormProps) {
	const [isHover, setIsHover] = useState<boolean>(false);
	const [addExerciseOpen, setAddExerciseOpen] = useState<boolean>(false);

	const [name, setName] = useState<string>(trainingPlan?.name || "");
	const [description, setDescription] = useState<string>(trainingPlan?.description || "");
	const [exercises, setExercises] = useState<ExerciseSimpleDTO[]>([...(trainingPlan?.exercises || [])]);

	const nameRef = useRef<any>(null);
	const descriptionRef = useRef<any>(null);


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

	const moveUp = (i: number) => {
		if (i === 0) return;
		const arr = [...exercises];
		[arr[i - 1], arr[i]] = [arr[i], arr[i - 1]];
		setExercises(arr);
	};

	const moveDown = (i: number) => {
		if (i === exercises.length - 1) return;
		const arr = [...exercises];
		[arr[i + 1], arr[i]] = [arr[i], arr[i + 1]];
		setExercises(arr);
	};

	const handleSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
		e.preventDefault();

		const isNameValid = nameRef.current?.validate();
		const isDescriptionValid = descriptionRef.current?.validate();

		if (!isNameValid || !isDescriptionValid) {
			return;
		}

		if (trainingPlan) {
			await handleUpdate();
		} else {
			await handleCreate();
		}
	}

	return (
		<>

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
					<form className="flex flex-col space-y-3 w-80 sm:w-100" onSubmit={handleSubmit}>
						<div className="flex flex-col text-left">
							<label htmlFor="name" className="text-sm mb-1">
								Name
							</label>
							<InputField
								ref={nameRef}
								value={name}
								onChange={setName}
								placeholder="Exercise name"
								rules={nameRules()}
							/>
						</div>

						<div className="flex flex-col text-left">
							<label htmlFor="description" className="text-sm mb-1">
								Description
							</label>
							<InputField
								ref={descriptionRef}
								value={description}
								onChange={setDescription}
								placeholder="Exercise description"
								rules={descriptionRules()}
								multiline={true}
							/>
						</div>

						{trainingPlan &&
							<div>
								<div className="flex flex-row justify-between items-center">
									<label htmlFor="description" className="text-sm mb-1 mt-1">
										Exercises
									</label>

									<button
										type="button"
										onClick={() => setAddExerciseOpen(true)}
										onMouseEnter={() => setIsHover(true)}
										onMouseLeave={() => setIsHover(false)}
										className="relative h-6 w-32 overflow-hidden text-md"
									>
										<span
											className={`absolute inset-0 flex items-center justify-end transition-all duration-200
              										${isHover ? "translate-x-full opacity-0" : "translate-x-0 opacity-100"}`}
										>
											+
										</span>

										<span
											className={`absolute inset-0 flex items-center justify-center transition-all duration-200
              										${isHover ? "translate-x-0 opacity-100 rounded-full bg-secondary/20 cursor-pointer" : "translate-x-full opacity-0"}`}
										>
											Add exercise +
										</span>

									</button>

								</div>

								<div className="overflow-y-auto max-h-75 border form-input p-0">
									{exercises.length > 0 ?
										(
											exercises.map((exercise, index) => (
												<div
													key={exercise.id}
													className={`${index % 2 === 0 ? 'bg-gray-400/5' : 'bg-background/50'} p-2 flex justify-between items-center gap-2`}>
													<span>{index + 1}. {exercise.name}</span>

													<div className="flex justify-center items-center gap-2">
														<div className="text-xs text-gray-400">
															{exercise.type}
														</div>
														<div className="flex flex-col items-center -mr-1">
															{index > 0 ?
																(<button
																	type="button"
																	onClick={() => moveUp(index)}
																	className="p-0.5 text-gray-400 hover:text-gray-600"
																>
																	<FaArrowUp size={10} />
																</button>) :
																(<></>)
															}

															{index < exercises.length - 1 ?
																(<button
																	type="button"
																	onClick={() => moveDown(index)}
																	className="p-0.5 text-gray-400 hover:text-gray-600"
																>
																	<FaArrowDown size={10} />
																</button>) :
																(<></>)
															}
														</div>

														<button
															type="button"
															onClick={() => {
																setExercises(exercises.filter((_, i) => i !== index));
															}}
															className="text-red-400/80 hover:rounded-full hover:bg-red-500/20 p-1 cursor-pointer">
															<FaRegTrashAlt />
														</button>
													</div>
												</div>
											))
										) : (
											<div>
												<p className="p-2 text-center text-gray-500 cursor-default">No exercises added yet. Exercises can be added from the respective exercise page or directly from form.</p>
											</div>
										)}
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

			<Modal isOpen={addExerciseOpen} onClose={() => setAddExerciseOpen(false)}>
				<SearchList<ExerciseSimpleDTO>
					fetchItems={exerciseService.getExercises}
					onSelect={(exercise) => {
						setExercises(prev => [...prev, exercise]);
						setAddExerciseOpen(false);
					}}
				/>
			</Modal>
		</>
	);
}

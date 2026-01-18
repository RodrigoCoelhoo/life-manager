import { useEffect, useRef, useState } from "react";
import ToggleButton from "../common/ToggleButton";
import { FaArrowDown, FaArrowUp, FaRegTrashAlt } from "react-icons/fa";
import type { TrainingPlanResponseDTO } from "../../services/training/training-plan/training-plan.dto";
import { Modal } from "../common/Modal";
import { SearchList } from "../common/SearchList";
import { trainingPlanService } from "../../services/training/training-plan/training-plan.service";
import { DateTimeField } from "../common/DateTimeField";
import { FaEye } from "react-icons/fa";
import { FaPencil } from "react-icons/fa6";
import type { ExerciseDetailsDTO, ExerciseSimpleDTO } from "../../services/training/exercise/exercise.dto";
import { trainingSessionService } from "../../services/training/training-session/training-session.service";
import type { SessionDetailsDTO, SessionExerciseSetRepDTO, SessionExerciseTimeDTO, TrainingSessionDTO } from "../../services/training/training-session/training-session.dto";
import { exerciseService } from "../../services/training/exercise/exercise.service";

interface TrainingSessionFormProps {
	trainingSessionId?: number;
	onClose: () => void;
	onCreate?: (trainingSession: TrainingSessionDTO) => Promise<void>;
	onUpdate?: (id: number, data: TrainingSessionDTO) => Promise<void>;
	onDelete?: (id: number) => Promise<void>;
}

export const TrainingSessionForm = ({ trainingSessionId, onClose, onCreate, onDelete, onUpdate }: TrainingSessionFormProps) => {
	const [isHover, setIsHover] = useState<boolean>(false);
	const [isEditing, setIsEditing] = useState<boolean>(trainingSessionId ? false : true);
	const [trainingPlanOptions, setTrainingPlanOptions] = useState<boolean>(false);
	const [addExerciseOpen, setAddExerciseOpen] = useState<boolean>(false);
	const [submitting, setSubmitting] = useState<boolean>(false);

	const [exercises, setExercises] = useState<ExerciseDetailsDTO[]>([]);
	const [date, setDate] = useState<string>("");
	const [time, setTime] = useState<string>("");

	const dateRef = useRef<any>(null);
	const timeRef = useRef<any>(null);

	const [openExercises, setOpenExercises] = useState<Record<number, boolean>>({});

	const toggleExercise = (id: number) => {
		setOpenExercises(prev => ({
			...prev,
			[id]: !prev[id]
		}));
	};

	const handleCreate = async () => {
		if (!onCreate) return;
		const session = getTrainingSessionDTO();
		await onCreate(session);
		onClose();
	};

	const handleUpdate = async () => {
		if (!onUpdate || !trainingSessionId) return;

		const session = getTrainingSessionDTO();
		await onUpdate(trainingSessionId, session);
		onClose();
	};

	const handleDelete = async () => {
		if (!onDelete || !trainingSessionId) return;
		await onDelete(trainingSessionId);
		onClose();
	};

	const handleSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
		e.preventDefault();

		const isDateValid = dateRef.current?.validate();
		const isTimeValid = timeRef.current?.validate();

		if (!isDateValid || !isTimeValid) {
			return;
		}

		setSubmitting(true);
		try {
			if (trainingSessionId) {
				await handleUpdate();
			} else {
				await handleCreate();
			}
		} catch (error) {
			console.error("Error submitting training session form:", error);
		} finally {
			setSubmitting(false);
		}
	};

	const fetchTrainingSession = async () => {
		try {
			if (trainingSessionId === undefined) return;
			const data: SessionDetailsDTO = await trainingSessionService.getTrainingSession(trainingSessionId);

			setDate(data.session.date.split("T")[0]);
			setTime(data.session.date.split("T")[1].substring(0, 5));
			setExercises(data.exercises);
		} catch (err) {
			console.error(err);
		}
	};

	useEffect(() => {
		fetchTrainingSession();
	}, []);

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

	const secondsToTime = (secs: number) => {
		const hours = Math.floor(secs / 3600);
		const minutes = Math.floor((secs % 3600) / 60);
		const seconds = secs % 60;
		return [hours, minutes, seconds]
			.map(v => v.toString().padStart(2, '0'))
			.join(':');
	};

	const timeToSeconds = (timeStr: string) => {
		const parts = timeStr.split(':').map(Number);
		if (parts.length === 3) {
			return parts[0] * 3600 + parts[1] * 60 + parts[2];
		} else if (parts.length === 2) {
			return parts[0] * 60 + parts[1];
		} else if (parts.length === 1) {
			return parts[0];
		}
		return 0;
	};

	const getTrainingSessionDTO = (): TrainingSessionDTO => {
		const localDateTime = `${date}T${time}`;

		return {
			date: localDateTime,
			exercises: exercises.map(ex => ({
				exerciseId: ex.id,
				sets: ex.sets.map(set => ({
					setNumber: "setNumber" in set ? set.setNumber : null,
					reps: "reps" in set ? set.reps : null,
					weight: "weight" in set ? set.weight : null,
					durationSecs: "durationSecs" in set ? set.durationSecs : null,
					distance: "distance" in set ? set.distance : null
				}))
			}))
		};
	};

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
			text-textcolor
			"
			>
				<div className="flex flex-row absolute top-2 right-4 gap-3 items-center">
					{trainingSessionId && (
						<>
							{isEditing && (
								<span className="text-sm text-gray-400">You are in edit mode</span>
							)}

							{isEditing ? <FaPencil /> : <FaEye />}

							<ToggleButton onToggle={setIsEditing} />
						</>
					)}


					<button
						onClick={onClose}
						className=" text-white text-xl hover:text-gray-200 p-2 cursor-pointer"
					>
						✕
					</button>
				</div>

				<form className="flex flex-col space-y-3 w-80 sm:w-100" onSubmit={handleSubmit}>
					<div className="mt-2 flex flex-row gap-6">
						<div className="flex flex-col">
							<label htmlFor="date" className="text-sm mb-1">
								Date
							</label>
							<DateTimeField
								ref={dateRef}
								type="date"
								value={date}
								onChange={setDate}
								rules={[val => (!!val ? true : "Date is required")]}
								disabled={!isEditing}
							/>
						</div>
						<div className="flex flex-col">
							<label htmlFor="time" className="text-sm mb-1">
								Time
							</label>
							<DateTimeField
								ref={timeRef}
								type="time"
								value={time}
								onChange={setTime}
								rules={[val => (!!val ? true : "Time is required")]}
								disabled={!isEditing}
							/>
						</div>
					</div>


					<button
						type="button"
						onClick={() => setTrainingPlanOptions(true)}
						disabled={!isEditing}
						hidden={!isEditing}
						className="form-submit mt-3"
					>
						Import Training Plan
					</button>

					<div className="flex flex-row justify-between items-center">
						<label htmlFor="description" className="text-sm mt-1">
							Exercises
						</label>

						<button
							type="button"
							onClick={() => setAddExerciseOpen(true)}
							onMouseEnter={() => setIsHover(true)}
							onMouseLeave={() => setIsHover(false)}
							className="relative h-6 w-32 overflow-hidden text-md"
							hidden={!isEditing}
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
					{exercises.length === 0 ?
						(
							<div className="h-15 form-input flex justify-center items-center">
								No exercises added yet.
							</div>
						) : (
							<ul className="max-h-85 overflow-y-auto flex flex-col font-extralight gap-2">
								{exercises.map((exercise, index) => {
									const isOpen = openExercises[exercise.id] ?? false;
									const isSetRep = exercise.type === "SET_REP";

									return (
										<li key={exercise.id} className="flex flex-col bg-primary/10 p-2 hover:bg-primary/20">
											<div
												onClick={() => toggleExercise(exercise.id)}
												className="cursor-pointer flex justify-between w-full text-left group-hover:bg-primary/20 rounded-md transition"
											>
												<div className="flex flex-row gap-2 items-center">
													<span>{exercise.name}</span>
												</div>

												<div className="flex flex-row gap-2 items-center text-xs text-gray-400">
													<span>{exercise.type}</span>
													{isEditing && (
														<div className="flex flex-col items-center -mr-1">
															{index > 0 ?
																(<button
																	type="button"
																	onClick={(e) => {
																		e.stopPropagation();
																		moveUp(index);
																	}}
																	className="p-0.5 text-gray-400 hover:text-gray-600"
																>
																	<FaArrowUp size={10} />
																</button>) :
																(<></>)
															}

															{index < exercises.length - 1 ?
																(<button
																	type="button"
																	onClick={(e) => {
																		e.stopPropagation();
																		moveDown(index);
																	}}
																	className="p-0.5 text-gray-400 hover:text-gray-600"
																>
																	<FaArrowDown size={10} />
																</button>) :
																(<></>)
															}
														</div>
													)}
													<button
														type="button"
														onClick={() => {
															setExercises(exercises.filter((_, i) => i !== index));
														}}
														hidden={!isEditing}
														className="text-red-400/80 hover:rounded-full hover:bg-red-500/20 p-1 cursor-pointer">
														<FaRegTrashAlt />
													</button>

													<span className={`text-lg transition-transform ${isOpen ? "rotate-90" : ""}`}>
														▸
													</span>
												</div>
											</div>

											{isOpen && (
												<div className="mt-2 p-2 flex flex-col gap-2 bg-foreground border border-secondary rounded-md">

													<div className={`grid ${isEditing ? "grid-cols-[60px_80px_80px_30px] sm:grid-cols-[60px_120px_120px_30px]" : "grid-cols-[60px_95px_95px_0px] sm:grid-cols-[60px_135px_135px_0px]"} items-center gap-2 text-xs font-semibold text-gray-500`}>
														<span>#</span>
														<span>{isSetRep ? "Reps" : "Duration"}</span>
														<span>{isSetRep ? "Weight" : "Distance (m)"}</span>
														<span></span>
													</div>

													{exercise.sets.map((set, setIndex) => {
														const handleChange = (field: string, value: any) => {
															setExercises(prev => {
																const updated = [...prev];
																const ex = updated[index];
																ex.sets = ex.sets.map((s, i) => (i === setIndex ? { ...s, [field]: value } : s));
																return updated;
															});
														};

														const removeSet = () => {
															setExercises(prev =>
																prev.map((ex, i) => {
																	if (i !== index) return ex;

																	const newSets = ex.sets
																		.filter((_, si) => si !== setIndex)
																		.map((s, si) =>
																			isSetRep ? { ...(s as SessionExerciseSetRepDTO), setNumber: si + 1 } : s
																		);

																	return { ...ex, sets: newSets };
																})
															);
														};

														const s = set as SessionExerciseSetRepDTO | SessionExerciseTimeDTO;

														return (
															<div
																key={setIndex}
																className={`grid ${isEditing ? "grid-cols-[60px_80px_80px_30px] sm:grid-cols-[60px_120px_120px_30px]" : "grid-cols-[60px_95px_95px_0px] sm:grid-cols-[60px_135px_135px_0px]"} items-center gap-2 text-xs`}
															>
																<span>{isSetRep ? `Set ${setIndex + 1}` : `Time ${setIndex + 1}`}</span>

																<input
																	type={isSetRep ? "number" : "time"}
																	step={isSetRep ? undefined : 1}
																	value={isSetRep
																		? (s as SessionExerciseSetRepDTO).reps
																		: secondsToTime((s as SessionExerciseTimeDTO).durationSecs)
																	}
																	disabled={!isEditing}
																	onChange={(e) =>
																		handleChange(
																			isSetRep ? "reps" : "durationSecs",
																			isSetRep ? Number(e.target.value) : timeToSeconds(e.target.value)
																		)
																	}
																	className="form-input"
																/>

																<input
																	type="number"
																	value={isSetRep ? (s as SessionExerciseSetRepDTO).weight : (s as SessionExerciseTimeDTO).distance}
																	disabled={!isEditing}
																	onChange={(e) =>
																		handleChange(
																			isSetRep ? "weight" : "distance",
																			Number(e.target.value)
																		)
																	}
																	className="form-input"
																/>

																{isEditing && (
																	<button
																		type="button"
																		onClick={removeSet}
																		className="text-red-400 hover:text-red-500 ml-2"
																	>
																		<FaRegTrashAlt />
																	</button>
																)}
															</div>
														);
													})}

													{isEditing && (
														<button
															type="button"
															className="w-full text-xs bg-primary/20 hover:bg-primary/40 px-2 py-1 rounded-md cursor-pointer"
															onClick={() =>
																setExercises(prev => {
																	return prev.map((ex, i) => {
																		if (i !== index) return ex;

																		const newSet = isSetRep
																			? { setNumber: ex.sets.length + 1, reps: 0, weight: 0 } as SessionExerciseSetRepDTO
																			: { durationSecs: 0, distance: 0 } as SessionExerciseTimeDTO;

																		return {
																			...ex,
																			sets: [...ex.sets, newSet]
																		};
																	});
																})
															}

														>
															+ Add Set
														</button>
													)}
												</div>
											)
											}
										</li>
									);
								})}
							</ul>)
					}

					<button
						type="submit"
						className="form-submit"
						disabled={submitting}
					>
						{trainingSessionId ? (submitting ? "Saving" : "Save Changes") : (submitting ? "Creating" : "Create")}
					</button>

					{isEditing && trainingSessionId &&
						<div className="border-t border-secondary/50">
							<button
								className="form-submit bg-red-500 w-full hover:bg-red-600"
								onClick={handleDelete}
							>
								Delete Training Session
							</button>
						</div>
					}
				</form>
			</div >

			<Modal isOpen={trainingPlanOptions} onClose={() => setTrainingPlanOptions(false)}>
				<SearchList<TrainingPlanResponseDTO>
					fetchItems={trainingPlanService.getTrainingPlans}
					onSelect={(trainingPlan) => {
						setExercises(prev => {
							const prevIds = new Set(prev.map(ex => ex.id));

							const newOnes = trainingPlan.exercises.filter(
								ex => !prevIds.has(ex.id)
							);

							const returnList: ExerciseDetailsDTO[] = newOnes.map(ex => ({
								...ex,
								sets: []
							}));

							return [...prev, ...returnList];
						});

						setTrainingPlanOptions(false);
					}}
					onClose={() => setTrainingPlanOptions(false)}
				/>
			</Modal>

			<Modal isOpen={addExerciseOpen} onClose={() => setAddExerciseOpen(false)}>
				<SearchList<ExerciseSimpleDTO>
					fetchItems={exerciseService.getExercises}
					onSelect={(exercise) => {
						if (exercises.findIndex(ex => ex.id === exercise.id) === -1) {

							const newExercise: ExerciseDetailsDTO = {
								id: exercise.id,
								name: exercise.name,
								type: exercise.type,
								demoUrl: exercise.demoUrl,
								sets: []
							}

							setExercises(prev => [...prev, newExercise]);
						}
						setAddExerciseOpen(false);
					}}
					onClose={() => setAddExerciseOpen(false)}
				/>
			</Modal>
		</>
	);
};
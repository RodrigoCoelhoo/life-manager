import { useState } from "react";
import type { ExerciseDTO, ExerciseResponseDTO, ExerciseUpdateDTO } from "../../services/training/exercise/exercise.dto";

interface ExerciseFormProps {
	exercise?: ExerciseResponseDTO;
	onClose: () => void;
	onCreate?: (exercise: ExerciseDTO) => Promise<void>;
	onUpdate?: (id: number, data: ExerciseUpdateDTO) => Promise<void>;
	onDelete?: (id: number) => Promise<void>;
}

export default function ExerciseForm({ onClose, exercise, onCreate, onUpdate, onDelete }: ExerciseFormProps) {
	const [name, setName] = useState(exercise?.name || "");
	const [description, setDescription] = useState(exercise?.description || "");
	const [type, setType] = useState<"SET_REP" | "TIME">(exercise?.type || "SET_REP");
	const [demoUrl, setDemoUrl] = useState(exercise?.demoUrl || "");

	const handleUpdate = async () => {
		if (!onUpdate || !exercise) return;
		await onUpdate(exercise.id, { name, description, demoUrl });
		onClose();
	};

	const handleDelete = async () => {
		if (!onDelete || !exercise) return;
		await onDelete(exercise.id);
		onClose();
	};

	const handleCreate = async () => {
		if (!onCreate) return;
		await onCreate({ name, description, type, demoUrl });
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
				<form className="flex flex-col space-y-4 w-80 sm:w-100" onSubmit={exercise ? handleUpdate : handleCreate}>
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

					{
						!exercise ? (
							<div className="flex flex-col">
								<label htmlFor="Type" className="text-sm mb-1">
									Type
								</label>
								<div className="flex flex-row ">
									<button
										type="button"
										className={`w-50 p-2 border border-secondary/50 rounded-l-lg ${type === "SET_REP" ? "bg-primary border-primary" : "bg-background hover:bg-secondary/20"}`}
										onClick={() => setType("SET_REP")}
									>
										SET_REP
									</button>
									<button
										type="button"
										className={`w-50 p-2 border border-secondary/50 rounded-r-lg ${type === "TIME" ? "bg-primary border-primary" : "bg-background hover:bg-secondary/20"}`}
										onClick={() => setType("TIME")}
									>
										TIME
									</button>
								</div>
							</div>
						) : <></>
					}

					<div className="flex flex-col text-left">
						<label htmlFor="demoUrl" className="text-sm mb-1">
							Demo Url
						</label>
						<input
							type="url"
							id="demoUrl"
							name="demoUrl"
							placeholder="Exercise demo"
							className="form-input"
							value={demoUrl}
							onChange={(event) => setDemoUrl(event.target.value)}
						/>
					</div>

					<button
						type="submit"
						className="form-submit"
					>
						{exercise ? "Save Changes" : "Create"}
					</button>
				</form>
			</div>

			{exercise &&
				<div className="border-t border-secondary/50">
					<button
						className="form-submit bg-red-500 w-full hover:bg-red-600"
						onClick={handleDelete}
					>
						Delete Exercise
					</button>
				</div>
			}
		</div>
	);
}

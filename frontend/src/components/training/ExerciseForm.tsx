import { useRef, useState } from "react";
import type { ExerciseDTO, ExerciseResponseDTO, ExerciseUpdateDTO } from "../../services/training/exercise/exercise.dto";
import { descriptionRules, nameRules, urlRules } from "../../rules/rules";
import { InputField } from "../common/InputField";

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

	const nameRef = useRef<any>(null);
	const descriptionRef = useRef<any>(null);
	const demoUrlRef = useRef<any>(null);

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

	const handleSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
		e.preventDefault();

		const isNameValid = nameRef.current?.validate();
		const isDescriptionValid = descriptionRef.current?.validate();
		const isDemoUrlValid = demoUrlRef.current?.validate();

		if (!isNameValid || !isDescriptionValid || !isDemoUrlValid) {
			return;
		}

		if (exercise) {
			await handleUpdate();
		} else {
			await handleCreate();
		}
	}

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
				<form className="flex flex-col space-y-4 w-80 sm:w-100" onSubmit={handleSubmit}>
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
						<InputField
							ref={demoUrlRef}
							value={demoUrl}
							onChange={setDemoUrl}
							placeholder="Exercise demo"
							rules={urlRules()}
							type="url"
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

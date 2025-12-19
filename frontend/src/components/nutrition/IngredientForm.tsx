import { useRef, useState } from "react";
import { nameRules } from "../../rules/rules";
import { InputField } from "../common/InputField";
import type { IngredientDTO, IngredientResponseDTO } from "../../services/nutrition/ingredient/ingredient.dto";

interface IngredientFormProps {
	ingredient?: IngredientResponseDTO;
	onClose: () => void;
	onCreate?: (ingredient: IngredientDTO) => Promise<void>;
	onUpdate?: (id: number, data: IngredientDTO) => Promise<void>;
	onDelete?: (id: number) => Promise<void>;
}

export default function IngredientForm({ ingredient, onClose, onCreate, onUpdate, onDelete }: IngredientFormProps) {
	const [submitting, setSubmitting] = useState<boolean>(false);
	const [name, setName] = useState(ingredient?.name || "");

	const nameRef = useRef<any>(null);

	const handleUpdate = async () => {
		if (!onUpdate || !ingredient) return;
		await onUpdate(ingredient.id, { name });
		onClose();
	};

	const handleDelete = async () => {
		if (!onDelete || !ingredient) return;
		await onDelete(ingredient.id);
		onClose();
	};

	const handleCreate = async () => {
		if (!onCreate) return;
		await onCreate({ name });
		onClose();
	};

	const handleSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
		e.preventDefault();

		const isNameValid = nameRef.current?.validate();

		if (!isNameValid) {
			return;
		}

		setSubmitting(true);
		try {
			if (ingredient) {
				await handleUpdate();
			} else {
				await handleCreate();
			}
		} catch (error) {
			console.error("Error submitting ingredient form:", error);
		} finally {
			setSubmitting(false);
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
							placeholder="Ingredient name"
							rules={nameRules()}
						/>
					</div>

					<button
						type="submit"
						className="form-submit"
						disabled={submitting}
					>
						{ingredient ? (submitting ? "Saving" : "Save Changes") : (submitting ? "Creating" : "Create")}
					</button>
				</form>
			</div>

			{ingredient &&
				<div className="border-t border-secondary/50">
					<button
						className="form-submit bg-red-500 w-full hover:bg-red-600"
						onClick={handleDelete}
					>
						Delete Ingredient
					</button>
				</div>
			}
		</div>
	);
}

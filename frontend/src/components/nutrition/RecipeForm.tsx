import { useRef, useState } from "react";
import { nameRules } from "../../rules/rules";
import { InputField } from "../common/InputField";
import { Select } from "../common/Select";
import { FaRegTrashAlt } from "react-icons/fa";
import { Unit, type RecipeDetailsDTO, type RecipeDTO, type RecipeIngredientResponseDTO } from "../../services/nutrition/recipe/recipe.dto";
import { SearchList } from "../common/SearchList";
import { ingredientService } from "../../services/nutrition/ingredient/ingredient.service";
import { Modal } from "../common/Modal";

interface RecipeFormProps {
	recipe?: RecipeDetailsDTO;
	onClose: () => void;
	onCreate?: (data: RecipeDTO) => Promise<void>;
	onUpdate?: (id: number, updatedData: RecipeDTO) => Promise<void>;
	onDelete?: (id: number) => Promise<void>;
}

export default function IngredientBrandForm({
	recipe,
	onClose,
	onCreate,
	onUpdate,
	onDelete,
}: RecipeFormProps) {
	const [submitting, setSubmitting] = useState<boolean>(false);
	const [name, setName] = useState<string>(recipe?.name || "");
	const [ingredients, setIngredients] = useState<RecipeIngredientResponseDTO[]>(recipe?.ingredients || []);

	const [isModalOpen, setIsModalOpen] = useState(false);

	const nameRef = useRef<any>(null);

	const handleCreate = async () => {
		if (!onCreate) return;

		await onCreate(
			{
				name,
				ingredients: ingredients.map(i => ({
					id: i.ingredient.id,
					amount: i.amount,
					unit: i.unit,
				}))
			});
		onClose();
	};

	const handleUpdate = async () => {
		if (!onUpdate || !recipe) return;

		await onUpdate(
			recipe.id,
			{
				name,
				ingredients: ingredients.map(i => ({
					id: i.ingredient.id,
					amount: i.amount,
					unit: i.unit,
				}))
			});
		onClose();
	};

	const handleDelete = async () => {
		if (!onDelete || !recipe) return;
		await onDelete(recipe.id);
		onClose();
	};

	const handleSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
		e.preventDefault();

		const isNameValid = nameRef.current?.validate();
		if (!isNameValid) return;

		setSubmitting(true);
		try {
			if (recipe) {
				await handleUpdate();
			} else {
				await handleCreate();
			}
		} catch (err) {
			console.error("Error submitting recipe form:", err);
		} finally {
			setSubmitting(false);
		}
	};

	const units: Unit[] = Object.keys(Unit) as Unit[];

	return (
		<>
			<div className="bg-foreground rounded-xl shadow-lg p-12 max-h-[85vh] overflow-y-auto text-textcolor">
				<button
					onClick={onClose}
					className="absolute top-2 right-4 text-xl hover:text-gray-200 p-2 cursor-pointer"
				>
					✕
				</button>

				<form
					className="flex flex-col space-y-4 w-80 sm:w-100"
					onSubmit={handleSubmit}
				>
					<div className="flex flex-col text-left">
						<label className="text-sm mb-1">Name</label>
						<InputField
							ref={nameRef}
							value={name}
							onChange={setName}
							placeholder="Brand name"
							rules={nameRules()}
						/>
					</div>

					<button type="button" className="form-submit mt-1" onClick={() => setIsModalOpen(true)}>
						Add Ingredients
					</button>

					<div className="flex flex-col gap-2 p-2 bg-background border rounded-lg border-primary mt-2">
						<div
							className={`grid grid-cols-[10fr_5fr_2fr_2fr] bg-background items-center`}
						>
							<div className="font-bold">Ingredient</div>
							<div className="font-bold">Amount</div>
							<div className="font-bold">Unit</div>
						</div>
						{ingredients.length !== 0 ?
							(ingredients.map((ingredient, index) => {
								const rowStyle = index % 2 === 0 ? "bg-foreground" : "bg-background";

								return (
									<div
										key={ingredient.ingredient.id}
										className={`grid grid-cols-[10fr_5fr_4fr_1fr] gap-2 p-0.5 ${rowStyle} items-center`}
									>
										<span>{ingredient.ingredient.name}</span>

										<InputField
											type="number"
											value={String(ingredient.amount ?? "")}
											onChange={(val) => {
												setIngredients(prev =>
													prev.map(i =>
														i.ingredient.id === ingredient.ingredient.id
															? { ...i, amount: Number(val) }
															: i
													)
												);
											}}
											placeholder="0"
										/>

										<Select
											value={ingredient.unit}
											items={units}
											getLabel={(u) => u}
											onChange={(unit) => {
												setIngredients(prev =>
													prev.map(i =>
														i.ingredient.id === ingredient.ingredient.id
															? { ...i, unit }
															: i
													)
												);
											}}
										/>

										<FaRegTrashAlt
											onClick={() => {
												setIngredients(prev =>
													prev.filter(i => i.ingredient.id !== ingredient.ingredient.id)
												);
											}}
											className="cursor-pointer hover:text-secondary"
										/>

									</div>
								);
							})) : (
								<p className="text-center text-gray-500 italic mt-2">No ingredients added yet.</p>
							)
						}
					</div>

					<button type="submit" className="form-submit" disabled={submitting}>
						{recipe
							? submitting
								? "Saving"
								: "Save Changes"
							: submitting
								? "Creating"
								: "Create"}
					</button>
				</form>

				{recipe && (
					<div className="border-t border-secondary/50 mt-4">
						<button
							className="form-submit bg-red-500 w-full hover:bg-red-600"
							onClick={handleDelete}
						>
							Delete Ingredient Brand
						</button>
					</div>
				)}
			</div>

			<Modal isOpen={isModalOpen} onClose={() => setIsModalOpen(false)}>
				<SearchList
					selectionMode="multiple"
					fetchItems={ingredientService.getIngredients}
					selectedIds={ingredients.map(i => i.ingredient.id)}
					selectedItems={ingredients.map(i => i.ingredient)}
					onChange={(items) => {
						setIngredients(prev => {
							const prevMap = new Map(prev.map(i => [i.ingredient.id, i]));
							return items.map(item =>
								prevMap.get(item.id) ?? {
									ingredient: item,
									amount: 0,
									unit: Unit.G,
								}
							);
						});
					}}
					onClose={() => setIsModalOpen(false)}
				/>
			</Modal>
		</>
	);
}

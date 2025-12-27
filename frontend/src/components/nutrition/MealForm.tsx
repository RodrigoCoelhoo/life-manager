import { useEffect, useRef, useState } from "react";
import ToggleButton from "../common/ToggleButton";
import { Modal } from "../common/Modal";
import { SearchList } from "../common/SearchList";
import { DateTimeField } from "../common/DateTimeField";
import { FaEye, FaRegTrashAlt } from "react-icons/fa";
import { FaPencil } from "react-icons/fa6";
import type { MealDTO, MealResponseDTO } from "../../services/nutrition/meal/meal.dto";
import { recipeService } from "../../services/nutrition/recipe/recipe.service";
import { Unit, type RecipeDetailsDTO } from "../../services/nutrition/recipe/recipe.dto";
import type { IngredientBrandResponseDTO } from "../../services/nutrition/ingredient-brand/ingredient-brand.dto";
import { ingredientService } from "../../services/nutrition/ingredient/ingredient.service";
import type { IngredientResponseDTO } from "../../services/nutrition/ingredient/ingredient.dto";
import { Select } from "../common/Select";
import { NumericField } from "../common/NumericField";

export interface MealIngredientFormModel {
	ingredientId: number;
	ingredientName: string;

	brandId: number | null;
	brands: IngredientBrandResponseDTO[];

	amount: number;
	unit: string;
}


interface MealFormProps {
	meal?: MealResponseDTO;
	onClose: () => void;
	onCreate?: (meal: MealDTO) => Promise<void>;
	onUpdate?: (id: number, data: MealDTO) => Promise<void>;
	onDelete?: (id: number) => Promise<void>;
}

export const MealForm = ({ meal, onClose, onCreate, onDelete, onUpdate }: MealFormProps) => {
	const [isHover, setIsHover] = useState<boolean>(false);
	const [isEditing, setIsEditing] = useState<boolean>(meal ? false : true);
	const [importRecipeOpen, setImportRecipeOpen] = useState<boolean>(false);
	const [addIngredientOpen, setAddIngredientOpen] = useState<boolean>(false);
	const [submitting, setSubmitting] = useState<boolean>(false);

	const [ingredients, setIngredients] = useState<MealIngredientFormModel[]>([]);
	const [date, setDate] = useState<string>(meal ? meal.dateTime.slice(0, 10) : "");
	const [time, setTime] = useState<string>(meal ? meal.dateTime.slice(11, 16) : "");

	const dateRef = useRef<any>(null);
	const timeRef = useRef<any>(null);

	const handleCreate = async () => {
		if (!onCreate) return;

		const localDateTime = `${date}T${time}`;
		await onCreate({
			date: localDateTime,
			ingredients: ingredients.map(i => ({
				ingredientId: i.ingredientId,
				brandId: i.brandId!,
				amount: i.amount,
				unit: i.unit
			}))
		});
		onClose();
	};

	const handleUpdate = async () => {
		if (!onUpdate || !meal) return;

		const localDateTime = `${date}T${time}`;
		await onUpdate(meal.id, {
			date: localDateTime,
			ingredients: ingredients.map(i => ({
				ingredientId: i.ingredientId,
				brandId: i.brandId!,
				amount: i.amount,
				unit: i.unit
			}))
		});
		onClose();
	};

	const handleDelete = async () => {
		if (!onDelete || !meal) return;
		await onDelete(meal.id);
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
			if (meal) {
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

	useEffect(() => {
		if (!meal) return;

		const loadIngredients = async () => {
			console.log("Loading ingredients for meal:", meal);
			const ingredientIds = meal.ingredients.map(i => i.ingredientId);

			const ingredientResponses: IngredientResponseDTO[] = await ingredientService.getIngredientsByIds(ingredientIds);

			const formIngredients: MealIngredientFormModel[] =
				meal.ingredients.map(mealIng => {
					const ingredient = ingredientResponses.find(
						i => i.id === mealIng.ingredientId
					);

					return {
						ingredientId: mealIng.ingredientId,
						ingredientName: mealIng.ingredient,

						brandId: mealIng.brandId,
						brands: ingredient?.brands ?? [],

						amount: mealIng.amount,
						unit: mealIng.unit
					};
				}
				);

			setIngredients(formIngredients);
		};

		loadIngredients();
	}, [meal]);

	const units: Unit[] = Object.keys(Unit) as Unit[];

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
				<div className="flex flex-row absolute top-2 right-4 gap-3 z-10 items-center">
					{meal && (
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
						onClick={() => setImportRecipeOpen(true)}
						disabled={!isEditing}
						hidden={!isEditing}
						className="form-submit mt-3"
					>
						Import Recipe
					</button>

					<div className="flex flex-col">
						<div className="flex flex-row justify-between items-center">
							<label htmlFor="description" className="text-sm mt-1">
								Ingredients
							</label>

							<button
								type="button"
								onClick={() => setAddIngredientOpen(true)}
								onMouseEnter={() => setIsHover(true)}
								onMouseLeave={() => setIsHover(false)}
								className="relative h-6 w-32 overflow-hidden text-md"
								hidden={!isEditing}
							>
								<span
									className={`cursor-pointer absolute inset-0 flex items-center justify-end transition-all duration-200
													${isHover ? "translate-x-full opacity-0" : "translate-x-0 opacity-100"}`}
								>
									+
								</span>

								<span
									className={`cursor-pointer absolute inset-0 flex items-center justify-center transition-all duration-200
													${isHover ? "translate-x-0 opacity-100 rounded-full bg-secondary/20 cursor-pointer" : "translate-x-full opacity-0"}`}
								>
									Add ingredient +
								</span>
							</button>
						</div>

						<div className="flex flex-col gap-2 mt-2">
							{ingredients.length > 0 ? (isEditing ? (ingredients.map((ing) => (
								<div
									key={ing.ingredientId}
									className="bg-primary/20 hover:bg-primary/40 px-2 py-1 rounded-md"
								>
									<div className="flex flex-row justify-between items-center">
										<h2 className="p-2 text-md font-semibold">{ing.ingredientName}</h2>
										{isEditing &&
											<FaRegTrashAlt
												onClick={() => {
													setIngredients(prev =>
														prev.filter(i => i.ingredientId !== ing.ingredientId)
													);
												}}
												className="cursor-pointer text-textcolor hover:text-secondary mr-2"
											/>
										}
									</div>
									<div className="flex flex-col bg-background p-2 gap-2">

										<div className="flex flex-col">
											<label className="text-sm mb-1">Brand</label>
											<Select<IngredientBrandResponseDTO>
												items={ing.brands}
												value={ing.brands.find(b => b.id === ing.brandId)}
												getLabel={b => b.name}
												disabled={!isEditing}
												onChange={(brand) => {
													setIngredients(prev => prev.map(i => {
														if (i.ingredientId === ing.ingredientId) {
															return {
																...i,
																brandId: brand ? brand.id : null
															};
														}
														return i;
													}));
												}}
											/>
										</div>

										<div className="flex flex-row gap-2">
											<div className="flex flex-col w-4/5">
												<label className="text-sm mb-1">Amount</label>
												<NumericField
													value={
														ing.amount != null
															? ing.amount.toFixed(2)
															: ""
													}
													disabled={!isEditing}
													onChange={(val) => {
														setIngredients(prev =>
															prev.map(i =>
																i.ingredientId === ing.ingredientId
																	? { ...i, amount: Number(val) }
																	: i
															)
														);
													}}
												/>
											</div>

											<div className="flex flex-col w-1/5">
												<label className="text-sm mb-1">Unit</label>
												<Select
													value={ing.unit.toUpperCase()}
													items={units}
													getLabel={(u) => u}
													onChange={(unit) => {
														setIngredients(prev =>
															prev.map(i =>
																i.ingredientId === ing.ingredientId
																	? { ...i, unit }
																	: i
															)
														);
													}}
													disabled={!isEditing}
												/>
											</div>
										</div>
									</div>
								</div>
							))) : (
								<div className="flex flex-col gap-2">
									{ingredients.map((ing, index) => (
										<div key={index} className="p-2 bg-primary/20 shadow-md rounded-md flex flex-row items-center justify-between">
											<div className="flex flex-col">
												<h2 className="text-lg">{ing.ingredientName}</h2>
												<span className="text-textcolor/80 font-extralight">{ing.brands.find(b => b.id === ing.brandId)?.name}</span>
											</div>
											<div className="flex flex-row">
												<span className="text-md font-base">{ing.amount.toFixed(2)}</span>
												<span className="ml-1 font-light text-textcolor/80">{ing.unit.toLowerCase()}</span>
											</div>
										</div>
									))}
								</div>
							)) : (
								<div className="p-2 bg-background border border-primary rounded-md shadow-md flex justify-center items-center">
									No ingredients added yet.
								</div>
							)}
						</div>
					</div>

					<button
						type="submit"
						className="form-submit"
						disabled={submitting}
					>
						{meal ? (submitting ? "Saving" : "Save Changes") : (submitting ? "Creating" : "Create")}
					</button>

					{isEditing && meal &&
						<div className="border-t border-secondary/50">
							<button
								className="form-submit bg-red-500 w-full hover:bg-red-600"
								onClick={handleDelete}
							>
								Delete Meal
							</button>
						</div>
					}
				</form>
			</div >

			<Modal isOpen={importRecipeOpen} onClose={() => setImportRecipeOpen(false)}>
				<SearchList<RecipeDetailsDTO>
					selectionMode="single"
					fetchItems={recipeService.getRecipes}
					onSelect={(recipe) => {
						setIngredients(
							recipe.ingredients.map((item) =>
							({
								ingredientId: item.ingredient.id,
								ingredientName: item.ingredient.name,

								brandId: null,
								brands: item.ingredient.brands,

								amount: item.amount,
								unit: item.unit
							}))
						);
					}}
					onClose={() => setImportRecipeOpen(false)}
				/>
			</Modal>

			<Modal isOpen={addIngredientOpen} onClose={() => setAddIngredientOpen(false)}>
				<SearchList
					selectionMode="multiple"
					fetchItems={ingredientService.getIngredients}
					selectedIds={ingredients.map(i => i.ingredientId)}
					selectedItems={ingredients.map(item => {
						return {
							id: item.ingredientId,
							name: item.ingredientName,
							brands: item.brands
						};
					})}
					onChange={(items) => {
						setIngredients(prev => {
							const prevMap = new Map(prev.map(i => [i.ingredientId, i]));
							return items.map(item =>
								prevMap.get(item.id) ?? {
									ingredientId: item.id,
									ingredientName: item.name,

									brandId: null,
									brands: item.brands,

									amount: 0,
									unit: Unit.G
								}
							);
						});
					}}
					onClose={() => setAddIngredientOpen(false)}
				/>
			</Modal>
		</>
	);
};
import { useRef, useState } from "react";
import { nameRules } from "../../rules/rules";
import { InputField } from "../common/InputField";
import {
	NutritionalTag,
	NutritionalTagUnit,
	type IngredientBrandDetailsResponseDTO,
	type IngredientBrandDTO,
	type NutritionalValueDTO,
} from "../../services/nutrition/ingredient-brand/ingredient-brand.dto";
import { Select } from "../common/Select";
import { FaRegTrashAlt } from "react-icons/fa";

interface IngredientBrandFormProps {
	ingredientId: number,
	brand?: IngredientBrandDetailsResponseDTO;
	onClose: () => void;
	onCreate?: (ingredientId: number, data: IngredientBrandDTO) => Promise<void>;
	onUpdate?: (ingredientId: number, brandId: number, updatedData: IngredientBrandDTO) => Promise<void>;
	onDelete?: (ingredientId: number, brandId: number) => Promise<void>;
}

export default function IngredientBrandForm({
	ingredientId,
	brand,
	onClose,
	onCreate,
	onUpdate,
	onDelete,
}: IngredientBrandFormProps) {
	const [submitting, setSubmitting] = useState(false);
	const [name, setName] = useState(brand?.name ?? "");

	const [selectedTags, setSelectedTags] = useState<NutritionalTag[]>(
		brand ? brand.nutritionalValues.map(v => v.nutrient) : []
	);

	const [nutritionalValues, setNutritionalValues] = useState<
		Record<NutritionalTag, NutritionalValueDTO>
	>(() => {
		if (!brand) return {};

		return Object.fromEntries(
			brand.nutritionalValues.map(v => [
				v.nutrient,
				{
					type: v.nutrient,
					per100units: v.per100units,
				},
			])
		);
	});

	const nameRef = useRef<any>(null);

	const addTag = (tag: NutritionalTag) => {
		setSelectedTags(prev => [...prev, tag]);

		setNutritionalValues(prev => ({
			...prev,
			[tag]: {
				type: tag,
				per100units: 0,
			},
		}));
	};

	const updatePer100Units = (tag: NutritionalTag, val: string) => {
		setNutritionalValues(prev => ({
			...prev,
			[tag]: {
				...prev[tag],
				per100units: Number(val),
			},
		}));
	};


	const removeTag = (tag: NutritionalTag) => {
		setSelectedTags(prev => prev.filter(t => t !== tag));

		setNutritionalValues(prev => {
			const copy = { ...prev };
			delete copy[tag];
			return copy;
		});
	};

	const handleCreate = async () => {
		if (!onCreate) return;

		await onCreate(
			ingredientId,
			{
				name,
				nutritionalValues: Object.values(nutritionalValues),
			});
		onClose();
	};

	const handleUpdate = async () => {
		if (!onUpdate || !brand) return;

		await onUpdate(
			ingredientId,
			brand.id,
			{
				name,
				nutritionalValues: Object.values(nutritionalValues),
			});
		onClose();
	};

	const handleDelete = async () => {
		if (!onDelete || !brand) return;
		console.log("Its Deleting")
		await onDelete(ingredientId, brand.id);
		onClose();
	};

	const handleSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
		e.preventDefault();

		const isNameValid = nameRef.current?.validate();
		if (!isNameValid) return;

		setSubmitting(true);
		try {
			if (brand) {
				await handleUpdate();
			} else {
				await handleCreate();
			}
		} catch (err) {
			console.error("Error submitting ingredient brand form:", err);
		} finally {
			setSubmitting(false);
		}
	};

	const nutritionalTags: NutritionalTag[] = Object.keys(NutritionalTag) as NutritionalTag[];

	return (
		<div className="bg-foreground rounded-xl shadow-lg p-12 max-h-[85vh] overflow-y-auto text-textcolor">
			<button
				onClick={onClose}
				className="absolute top-2 right-4 text-xl hover:text-gray-200 p-2"
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

				<Select
					multiple
					fixedLabel="Select Nutrients"
					value={selectedTags}
					items={nutritionalTags}
					onChange={(tags) => {
						const added = tags.find(t => !selectedTags.includes(t));
						const removed = selectedTags.find(t => !tags.includes(t));

						if (added) addTag(added);
						if (removed) removeTag(removed);
					}}
					getLabel={(tag) => tag.replaceAll("_", " ")}
				/>

				<div className="flex flex-col gap-2 p-2 bg-background border rounded-lg border-primary mt-4">
					{selectedTags.map((tag, index) => {
						const value = nutritionalValues[tag];
						const rowStyle =
							index % 2 === 0 ? "bg-background" : "bg-foreground";

						return (
							<div
								key={tag}
								className={`grid grid-cols-[10fr_10fr] p-0.5 ${rowStyle} items-center`}
							>
								<span>{tag}</span>
								<div className="flex flex-row gap-3 items-center">
									<div className="grid grid-cols-[10fr_2fr] items-center">
										<InputField
											type="number"
											value={String(value.per100units ?? "")}
											onChange={(val) => updatePer100Units(tag, val)}
											placeholder="0"
										/>
										<span className="text-sm text-gray-400 text-right">
											{NutritionalTagUnit[tag]}
										</span>
									</div>

									{tag !== "CALORIES" ?
										(<FaRegTrashAlt
											onClick={() => removeTag(tag)}
											className="cursor-pointer hover:text-secondary"
										/>
										) : (
											<FaRegTrashAlt
												className="text-muted"
											/>
										)
									}
								</div>
							</div>
						);
					})
					}
				</div>

				<button type="submit" className="form-submit" disabled={submitting}>
					{brand
						? submitting
							? "Saving"
							: "Save Changes"
						: submitting
							? "Creating"
							: "Create"}
				</button>
			</form>

			{brand && (
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
	);
}

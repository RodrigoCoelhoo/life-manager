import { useState } from "react";
import type { IngredientDetailsDTO, IngredientDTO } from "../../services/nutrition/ingredient/ingredient.dto";
import { IoIosArrowForward } from "react-icons/io";
import { FaEdit } from "react-icons/fa";
import IngredientBrandCard from "./IngredientBrandCard";
import { Modal } from "../common/Modal";
import IngredientForm from "./IngredientForm";
import IngredientBrandForm from "./IngredientBrandForm";
import type { IngredientBrandDTO } from "../../services/nutrition/ingredient-brand/ingredient-brand.dto";
//import { Modal } from "../common/Modal";

interface IngredientCardProps extends IngredientDetailsDTO {
	onUpdate: (id: number, data: IngredientDTO) => Promise<void>;
	onDelete: (id: number) => Promise<void>;
	onCreateBrand: (ingredientId: number, data: IngredientBrandDTO) => Promise<void>;
	onUpdateBrand: (ingredientId: number, brandId: number, updatedData: IngredientBrandDTO) => Promise<void>;
	onDeleteBrand: (ingredientId: number, brandId: number) => Promise<void>;
}

export default function IngredientCard({ onUpdate, onDelete, onCreateBrand, onUpdateBrand, onDeleteBrand, ...ingredient }: IngredientCardProps) {
	const [isOpen, setIsOpen] = useState(false);

	const [updateIngredientOpen, setUpdateIngredientOpen] = useState<boolean>(false);
	const [createIngredientBrandOpen, setCreateIngredientBrandOpen] = useState<boolean>(false);

	return (
		<>
			<div className="flex flex-col text-textcolor w-full">
				<div
					className={`flex flex-row justify-between items-center cursor-pointer ${isOpen ? "bg-primary/20" : "bg-foreground"} p-6 hover:bg-primary/20 hover:text-secondary shadow-lg`}
					onClick={() => setIsOpen(!isOpen)}
				>
					<div className="flex flex-row gap-3 items-center">
						<h2 className="text-xl font-bold">{ingredient.name}</h2>
						<span className="text-base font-extralight text-textcolor/80">{ingredient.brands.length} brand(s).</span>
					</div>

					<div className="flex flex-row gap-3 text-textcolor items-center">
						<div>
							<FaEdit
								className="hover:text-secondary cursor-pointer"
								size={16}
								onClick={(e) => {
									e.stopPropagation();
									setUpdateIngredientOpen(true);
								}}
							/>
						</div>
						<span className={`transition-transform ${isOpen ? "rotate-90" : ""}`}>
							<IoIosArrowForward />
						</span>
					</div>
				</div>

				{isOpen &&
					<div className="w-full h-full bg-foreground/90 px-10 py-4 flex flex-col gap-3">
						{ingredient.brands.map((item) => (
							<IngredientBrandCard
								key={item.id}
								{...item}
								ingredientId={ingredient.id}
								onUpdate={onUpdateBrand}
								onDelete={onDeleteBrand}
							/>
						))}
						<div className="flex flex-row items-center justify-center">
							<button
								onClick={(e) => {
									e.stopPropagation();
									setCreateIngredientBrandOpen(true);
								}}
								className="bg-primary/20 cursor-pointer hover:bg-primary/50 w-fit p-2 rounded-xl text-sm"
							>
								Add brand +
							</button>
						</div>

					</div>
				}
			</div>

			<Modal isOpen={updateIngredientOpen} onClose={() => setUpdateIngredientOpen(false)}>
				<IngredientForm
					onClose={() => setUpdateIngredientOpen(false)}
					ingredient={ingredient}
					onUpdate={onUpdate}
					onDelete={onDelete}
				/>
			</Modal>

			<Modal isOpen={createIngredientBrandOpen} onClose={() => setCreateIngredientBrandOpen(false)}>
				<IngredientBrandForm
					ingredientId={ingredient.id}
					onClose={() => setCreateIngredientBrandOpen(false)}
					onCreate={onCreateBrand}
				/>
			</Modal>
		</>
	);
}
